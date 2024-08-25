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
import org.g9project4.global.ListData;
import org.g9project4.global.Pagination;
import org.g9project4.publicData.tour.constants.ContentType;
import org.g9project4.publicData.tour.constants.OrderBy;
import org.g9project4.publicData.tour.controllers.TourPlaceSearch;
import org.g9project4.publicData.tour.entities.QTourPlace;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewTourPlaceInfoService {
    @PersistenceContext
    private EntityManager em;

    private final TourPlaceRepository tourPlaceRepository;
    private final HttpServletRequest request;

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
                    orBuilder.or(tourPlace.sigunguCode.eq(Integer.valueOf(sigunguCode)));
                }
            }
        }
        // ContentType
        if (search.getContentType() != null) {
            andBuilder.and(tourPlace.contentTypeId.eq(search.getContentType().getId()));
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
        OrderBy _orderBy = search.getOrderBy();
        OrderSpecifier order = null;
        if (_orderBy != null) {
            if (_orderBy.equals(OrderBy.title)) {
                order = tourPlace.title.asc();
            } else if (_orderBy.equals(OrderBy.modifiedTime)) {
                order = tourPlace.modifiedTime.desc();
            }
        }
        /* 정렬 조건 처리 E */
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        JPAQuery<TourPlace> query = queryFactory.selectFrom(tourPlace)
                .offset(offset)
                .limit(limit)
                .where(andBuilder);
        int count = queryFactory.selectFrom(tourPlace)
                .where(andBuilder).fetch().size();

        List<TourPlace> items = query.fetch();

        items.forEach(this::addInfo);

        if (_orderBy !=null && _orderBy.equals(OrderBy.distance)) {
            items.forEach(i -> addDistance(i, search));
            items.sort(Comparator.comparing(TourPlace::getDistance));
        }
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
        if (latitude != null && longitude != null) {
            distance = Math.sqrt(Math.pow(latitude - curLat, 2) + Math.pow(longitude - curLon, 2));
        }
        item.setDistance(distance);
    }
}
