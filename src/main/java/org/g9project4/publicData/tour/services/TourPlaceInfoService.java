package org.g9project4.publicData.tour.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.g9project4.global.ListData;
import org.g9project4.global.Pagination;
import org.g9project4.publicData.tour.exceptions.TourPlaceNotFoundException;
import org.g9project4.global.rests.gov.api.ApiItem;
import org.g9project4.global.rests.gov.api.ApiResult;
import org.g9project4.member.entities.Member;
import org.g9project4.publicData.myvisit.TourplaceDto;
import org.g9project4.publicData.tour.entities.GreenPlace;
import org.g9project4.publicData.tour.constants.ContentType;
import org.g9project4.publicData.tour.controllers.TourPlaceSearch;
import org.g9project4.publicData.tour.entities.QGreenPlace;
import org.g9project4.publicData.tour.entities.QTourPlace;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.g9project4.search.constatnts.SearchType;
import org.g9project4.search.entities.SearchHistory;
import org.g9project4.search.repositories.SearchHistoryRepository;
import org.g9project4.search.services.SearchHistoryService;
import org.g9project4.visitrecord.constants.RecommendType;
import org.g9project4.visitrecord.entities.VisitRecord;
import org.g9project4.visitrecord.entities.VisitRecordId;
import org.g9project4.visitrecord.repositories.VisitRecordRepository;
import org.g9project4.visitrecord.services.VisitRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TourPlaceInfoService {
    @PersistenceContext
    private EntityManager em;

    private final RestTemplate restTemplate;
    private final TourPlaceRepository repository;
    private String serviceKey = "n5fRXDesflWpLyBNdcngUqy1VluCJc1uhJ0dNo4sNZJ3lkkaYkkzSSY9SMoZbZmY7/O8PURKNOFmsHrqUp2glA==";
    private final HttpServletRequest request;
    private final VisitRecordService recordService;
    private final SearchHistoryService historyService;
    private final SearchHistoryRepository searchHistoryRepository;
    private final VisitRecordRepository visitRecordRepository;
    private final VisitRecordService visitRecordService;
    private final SearchHistoryService searchHistoryService;


    /* km 마이페이지 - 검색기록 방문기록 추천 S */
    public ListData<TourplaceDto> getTotalList(TourPlaceSearch search, RecommendType recommendType, Member loggedMember, String keyword) {

        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit < 1 ? 10 : limit;
        int offset = (page - 1) * limit;

        QTourPlace qTourPlace = QTourPlace.tourPlace;
        BooleanBuilder andBuilder = new BooleanBuilder();

        // 추천 검색 S
        if (recommendType != null) {
            if (recommendType == RecommendType.VIEW) {
                List<Long> contentIds = recordService.getMonthlyRecommend();
                if (contentIds != null && !contentIds.isEmpty()) {
                    andBuilder.and(qTourPlace.contentId.in(contentIds));
                }
            } else if (recommendType == RecommendType.KEYWORD) {
                List<String> keywords = historyService.getKeywords(SearchType.TOUR);
                if (keywords != null && !keywords.isEmpty()) {
                    BooleanBuilder orBuilder = new BooleanBuilder();
                    for (String currentKeyword : keywords) { // 변경된 변수 이름
                        orBuilder.or(qTourPlace.title.concat(qTourPlace.address).contains(currentKeyword.trim()));
                    }
                    andBuilder.and(orBuilder);
                }
            }
        }
        // 추천 검색 E


        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        List<TourplaceDto> items = queryFactory.selectFrom(qTourPlace)
                .orderBy(qTourPlace.contentId.asc())
                .offset(offset)
                .limit(limit)
                .fetch()
                .stream()
                .map(tourPlace -> {
                    int recordPoint = calculatePoint(tourPlace, loggedMember, keyword, tourPlace.getContentId());
                    // getPlacePointValue()가 null일 경우 0으로 대체
                    int placePointValue = tourPlace.getPlacePointValue() != null ? tourPlace.getPlacePointValue() : 0;

                    // 최종 점수 계산 (placePointValue + recordPoint)
                    int finalPointValue = placePointValue + recordPoint;

                    // DTO로 변환
                    return new TourplaceDto(
                            tourPlace.getContentId(),
                            tourPlace.getTitle(),
                            tourPlace.getAddress(),
                            tourPlace.getFirstImage(),
                            tourPlace.getTel(),
                            finalPointValue);
                })
                .sorted((tp1, tp2) -> Integer.compare(tp2.getFinalPointValue(), tp1.getFinalPointValue())) // 내림차순 정렬
                .skip(offset) // 페이징을 위해 offset 적용
                .limit(limit) // 페이징을 위해 limit 적용
                .collect(Collectors.toList());

        Pagination pagination = new Pagination(page, (int) repository.count(andBuilder), 0, limit, request);

        return new ListData<>(items, pagination);
    }

    // 포인트 계산 메서드
    private int calculatePoint(TourPlace tourPlace,  Member loggedMember, String keyword, Long contentId) {
        // 검색 횟수 및 방문 횟수를 기반으로 포인트를 계산
        long searchCount = getSearchCountForMember(loggedMember, keyword);
        long visitCount = getVisitCount(contentId);

        // 검색 및 방문에 대한 포인트 계산
        int searchPoints = (int) searchCount * 5;
        int visitPoints = (int) visitCount * 10;

        // 최종 포인트 계산
        int recordPoint = searchPoints + visitPoints;

        return recordPoint;
    }

    public int getVisitCount(Long contentId) {
        int uid = recordService.getUid(); // 현재 로그인한 회원의 uid를 가져옵니다.
        LocalDate yearMonth = recordService.thisMonth(); // 이번 달을 기준으로 yearMonth를 가져옵니다.

        // 복합 키로 VisitRecord를 조회합니다.
        VisitRecordId recordId = new VisitRecordId(contentId, uid);
        Optional<VisitRecord> visitRecordOptional = visitRecordRepository.findById(recordId);

        // VisitRecord가 존재하면 방문 횟수를 반환하고, 그렇지 않으면 0을 반환합니다.
        return visitRecordOptional.map(VisitRecord::getVisitCount).orElse(0L).intValue();
    }


    // 특정 회원과 키워드에 대한 검색 횟수를 가져오는 메서드
    public long getSearchCountForMember(Member loggedMember, String keyword) {
        SearchType searchType = SearchType.TOUR;

        Optional<SearchHistory> searchHistoryOptional = searchHistoryRepository
                .findByMKeySType(loggedMember, keyword, searchType);

        return searchHistoryOptional.map(SearchHistory::getSearchCount).orElse(0L);
    }



    /* km 추천 검색 E*/



    /*public ListData<GreenPlace> getGreenList(TourPlaceSearch search) {
        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit < 1 ? 20 : limit;
        int offset = page * limit + 1;
        *//* 검색 조건 처리 S *//*
        QGreenPlace greenPlace = QGreenPlace.greenPlace;
        BooleanBuilder andBuilder = new BooleanBuilder();

        String sopt = search.getSopt();
        String skey = search.getSkey();

        sopt = StringUtils.hasText(sopt) ? sopt.toUpperCase() : "ALL";

        if (StringUtils.hasText(skey)) {
            skey = skey.trim();

            BooleanExpression titleCond = greenPlace.title.contains(skey); // 제목 - subject LIKE '%skey%';
            BooleanExpression addressCond = greenPlace.address.contains(skey); // 내용 - content LIKE '%skey%';

            if (sopt.equals("TITLE")) { // 여행지 이름

                andBuilder.and(titleCond);

            } else if (sopt.equals("ADDRESS")) { // 주소

                andBuilder.and(addressCond);

            } else if (sopt.equals("TITLE_ADDRESS")) { // 제목 + 내용

                BooleanBuilder orBuilder = new BooleanBuilder();
                orBuilder.or(titleCond)
                        .or(addressCond);

                andBuilder.and(orBuilder);

            }

        }

        *//* 검색 조건 처리 E *//*
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        int count = queryFactory.selectFrom(greenPlace)
                .where(andBuilder).fetch().size();
        JPAQuery<GreenPlace> query = queryFactory.selectFrom(greenPlace)
                .orderBy(greenPlace.contentId.asc())
                .offset(offset)
                .limit(limit)
                .where(andBuilder);
        List<GreenPlace> items = query.fetch();
        Pagination pagination = new Pagination(page, count, 0, limit, request);
        return new ListData<>(items, pagination);
    }*/

    public ListData<TourPlace> getSearchedList(TourPlaceSearch search) {
        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        int offset = (page - 1) * limit;

        /* 검색 조건 처리 S */
        QTourPlace tourPlace = QTourPlace.tourPlace;
        BooleanBuilder andBuilder = new BooleanBuilder();
        if (search.getContentType() != null) {
//            andBuilder.and(tourPlace.contentTypeId.eq(search.getContentType().getId()));
        }
        String sopt = search.getSopt();
        String skey = search.getSkey();
        sopt = StringUtils.hasText(sopt) ? sopt.toUpperCase() : "ALL";

        if (StringUtils.hasText(skey)) {
            skey = skey.trim();

            BooleanExpression titleCond = tourPlace.title.contains(skey); // 제목 - subject LIKE '%skey%';
            BooleanExpression addressCond = tourPlace.address.contains(skey); // 내용 - content LIKE '%skey%';

            if (sopt.equals("TITLE")) { // 여행지 이름

                andBuilder.and(titleCond);

            } else if (sopt.equals("ADDRESS")) { // 주소

                andBuilder.and(addressCond);

            } else if (sopt.equals("TITLE_ADDRESS") || sopt.equals("ALL")) { // 제목 + 내용
                BooleanBuilder orBuilder = new BooleanBuilder();
                orBuilder.or(titleCond)
                        .or(addressCond);
                andBuilder.and(orBuilder);
            }

        }

        /* 검색 조건 처리 E */

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        int count = queryFactory.selectFrom(tourPlace)
                .where(andBuilder).fetch().size();
        JPAQuery<TourPlace> query = queryFactory.selectFrom(tourPlace)
                .orderBy(tourPlace.contentId.asc())
                .offset(offset)
                .limit(limit)
                .where(andBuilder);
        List<TourPlace> items = query.fetch();

        items.forEach(this::addInfo);

        Pagination pagination = new Pagination(page, count, 0, limit, request);
        return new ListData<>(items, pagination);
    }

    private void addInfo(TourPlace item) {
        Long contentTypeId = item.getContentTypeId();
        if (contentTypeId != null) {
            ContentType type = ContentType.getList().stream().filter(c -> c.getId() == contentTypeId.longValue()).findFirst().orElse(null);
            item.setContentType(type);
        }
    }

}