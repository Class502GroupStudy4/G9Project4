package org.g9project4.publicData.tour.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.g9project4.config.controllers.ApiConfig;
import org.g9project4.config.service.ConfigInfoService;
import org.g9project4.global.CommonSearch;
import org.g9project4.global.ListData;
import org.g9project4.global.Pagination;
import org.g9project4.global.Utils;
import org.g9project4.member.entities.Member;
import org.g9project4.publicData.myvisit.TourplaceDto;
import org.g9project4.publicData.tour.exceptions.TourPlaceNotFoundException;
import org.g9project4.global.rests.gov.api.ApiItem;
import org.g9project4.global.rests.gov.api.ApiResult;
import org.g9project4.publicData.tour.constants.ContentType;
import org.g9project4.publicData.tour.constants.OrderBy;
import org.g9project4.publicData.tour.controllers.TourPlaceSearch;
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
import org.g9project4.wishlist.constants.WishType;
import org.g9project4.wishlist.services.WishListService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourPlaceInfoService {
    private final RestTemplate restTemplate;

    @PersistenceContext
    private EntityManager em;

    private final JPAQueryFactory queryFactory;
    private final Utils utils;
    private final TourPlaceRepository tourPlaceRepository;
    private final HttpServletRequest request;
    private final ConfigInfoService configInfoService;
    private final VisitRecordService recordService;
    private final SearchHistoryService historyService;
    private final SearchHistoryRepository searchHistoryRepository;
    private final VisitRecordRepository visitRecordRepository;
    private final VisitRecordService visitRecordService;
    private final WishListService wishListService;
    /**
     * 필터 옵션
     * 1. 태그들
     * 1-1 AreaCode
     * 1.구 소분류(시군구 코드)
     * 1-2 ContentType
     * 1-3 Category
     * 1. category1
     * 2. category2
     * 3. category3
     * 2. 검색 키워드
     * 3. 정렬 이름순, 최신순, 거리순, 인기순
     *
     * @param search
     * @return
     */
    public ListData<TourPlace> getSearchedList(TourPlaceSearch search) {

        /* 검색 조건 처리 S */
        QTourPlace tourPlace = QTourPlace.tourPlace;
        BooleanBuilder andBuilder = new BooleanBuilder();
        BooleanBuilder orBuilder = new BooleanBuilder();
        //AreaCode
        if (StringUtils.hasText(search.getAreaCode())) {
            andBuilder.and(tourPlace.areaCode.eq(search.getAreaCode()));
            if (search.getSigunguCode() != null) {
                for (String sigunguCode : search.getSigunguCode()) {
                    orBuilder.or(tourPlace.sigunguCode.eq(sigunguCode));
                }
            }
        }
        // ContentType
        if (StringUtils.hasText(search.getContentType())) {
            andBuilder.and(tourPlace.contentTypeId.eq(utils.typeCode(search.getContentType()).getId()));
        }
        //카테고리
        if (StringUtils.hasText(search.getCategory1())) {
            andBuilder.and(tourPlace.category1.eq(search.getCategory1()));
            if (StringUtils.hasText(search.getCategory2())) {
                andBuilder.and(tourPlace.category2.eq(search.getCategory2()));
            }
            if (StringUtils.hasText(search.getCategory3())) {
                andBuilder.and(tourPlace.category3.eq(search.getCategory3()));
            }
        }

        //검색 키워드
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
                orBuilder.or(titleCond)
                        .or(addressCond);
            }

        }
        andBuilder.and(orBuilder);
        /* 검색 조건 처리 E */

        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        int offset = (page - 1) * limit;

        /* 정렬 조건 처리 S */
        String _orderBy = search.getOrderBy();
        OrderSpecifier order = tourPlace.contentId.asc();
        if (StringUtils.hasText(_orderBy)) {
            if (_orderBy.equals(OrderBy.title.name())) {
                order = tourPlace.title.asc();
            } else if (_orderBy.equals(OrderBy.modifiedTime.name())) {
                order = tourPlace.modifiedTime.desc();
            } else if (_orderBy.equals(OrderBy.distance.name())) {
                return listOrderByDistance(search);
            }
        }

        /* 인기순 인기점수로 내림차순 정열 시작 */
        if (StringUtils.hasText(_orderBy)) {
            if (_orderBy.equals(OrderBy.popularity.name())) {
                order = tourPlace.placePointValue.desc();
            } else if (_orderBy.equals(OrderBy.modifiedTime.name())) {
                order = tourPlace.modifiedTime.desc();
            }
        }
        /* 인기순 인기점수로 내림차순 정열 끝 */

        /* 정렬 조건 처리 E */
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        JPAQuery<TourPlace> query = queryFactory.selectFrom(tourPlace)
                .orderBy(order)
                .offset(offset)
                .limit(limit)
                .where(andBuilder);
        int count = queryFactory.selectFrom(tourPlace)
                .where(andBuilder).fetch().size();

        List<TourPlace> items = query.fetch();

        items.forEach(this::addInfo);
        Pagination pagination = new Pagination(page, count, 0, limit, request);
        return new ListData<>(items, pagination);

    }

    private void addInfo(TourPlace item) {
//        컨텐트 타입 정보
        Long contentTypeId = item.getContentTypeId();
        if (contentTypeId != null) {
            ContentType type = ContentType.getList().stream().filter(c -> c.getId() == contentTypeId.longValue()).findFirst().orElse(null);
            item.setContentType(type);
        }
    }

    private void addDistance(TourPlace item, TourPlaceSearch search) {
        // 거리 정보 주입
        Double latitude = item.getLatitude();
        Double longitude = item.getLongitude();
        Double curLat = search.getLatitude();
        Double curLon = search.getLongitude();

        Double distance = 0.0;
        if (latitude != null && longitude != null && curLat != null && curLon != null) {
            distance = Math.sqrt(Math.pow(latitude - curLat, 2) + Math.pow(longitude - curLon, 2));
        }
        item.setDistance(distance);
    }

    private ListData<TourPlace> listOrderByDistance(TourPlaceSearch search) {
        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        int offset = (page - 1) * limit;
        Double curLon = search.getLongitude();
        Double curLat = search.getLatitude();
        Integer radius = search.getRadius();
        String contentType = "";
        if (search.getContentType() != null && !search.getContentType().equals("")) {
            contentType = String.valueOf(utils.typeCode(search.getContentType()).getId());
        }
        ApiConfig apiKey = configInfoService.get("apiConfig", ApiConfig.class).orElseGet(ApiConfig::new);
        String url = String.format("https://apis.data.go.kr/B551011/KorService1/locationBasedList1?numOfRows=%d&pageNo=%d&MobileOS=and&MobileApp=test&_type=json&mapX=%f&mapY=%f&contentTypeId=%s&radius=%d&serviceKey=%s", limit, page, curLon, curLat, contentType, radius, apiKey.getPublicOpenApiKey());
        ResponseEntity<ApiResult> response = null;
        try {
            response = restTemplate.getForEntity(URI.create(url), ApiResult.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<ApiItem> items = response.getBody().getResponse().getBody().getItems().getItem();
                List<TourPlace> tourPlaces = new ArrayList<TourPlace>();
                try {
                    items.forEach(item -> {
                        String address = item.getAddr1();
                        address += StringUtils.hasText(item.getAddr2()) ? " " + item.getAddr2() : "";
                        TourPlace tourPlace = TourPlace.builder()
                                .contentId(item.getContentid())
                                .contentTypeId(item.getContenttypeid())
                                .category1(item.getCat1())
                                .category2(item.getCat2())
                                .category3(item.getCat3())
                                .modifiedTime(item.getModifiedtime())
                                .createdTime(item.getCreatedtime())
                                .title(item.getTitle())
                                .tel(item.getTel())
                                .address(address)
                                .areaCode(item.getAreacode())
                                .distance(item.getDist())
                                .bookTour(item.getBooktour().equals("1"))
                                .distance(item.getDist())
                                .firstImage(item.getFirstimage())
                                .firstImage2(item.getFirstimage2())
                                .cpyrhtDivCd(item.getCpyrhtDivCd())
                                .latitude(item.getMapy())
                                .longitude(item.getMapx())
                                .mapLevel(item.getMlevel())
                                .sigunguCode(item.getSigunguCode())
                                .build();
                        tourPlaces.add(tourPlace);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int count = response.getBody().getResponse().getBody().getTotalCount();
                Pagination pagination = new Pagination(page, count, 0, limit, request);
                return new ListData<>(tourPlaces, pagination);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public TourPlace get(Long contentId){
        return tourPlaceRepository.findById(contentId).orElseThrow(TourPlaceNotFoundException::new);
    }
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

        Pagination pagination = new Pagination(page, (int) tourPlaceRepository.count(andBuilder), 0, limit, request);

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
    /**
     * 내가 찜한 게시글 목록
     *
     * @param search
     * @return
     */
    public ListData<TourPlace> getWishList(CommonSearch search) {

        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit < 1 ? 10 : limit;
        int offset = (page - 1) * limit;


        List<Long> seqs = wishListService.getList(WishType.TOUR);
        if (seqs == null || seqs.isEmpty()) {
            return new ListData<>();
        }

        QTourPlace tourPlace = QTourPlace.tourPlace;
        BooleanBuilder andBuilder = new BooleanBuilder();
        andBuilder.and(tourPlace.contentId.in(seqs));

        List<TourPlace> items = queryFactory.selectFrom(tourPlace)
                .where(andBuilder)
//                .leftJoin(tourPlace.)
//                .fetchJoin()
                .offset(offset)
                .limit(limit)
                .orderBy(tourPlace.createdAt.desc())
                .fetch();

        long total = tourPlaceRepository.count(andBuilder);
        int ranges = utils.isMobile() ? 5 : 10;
        Pagination pagination = new Pagination(page, (int) total, ranges, limit, request);

        return new ListData<>(items, pagination);
    }
}
