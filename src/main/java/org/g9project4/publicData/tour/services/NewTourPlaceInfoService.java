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
import org.g9project4.publicData.tour.constants.ContentType;
import org.g9project4.publicData.tour.controllers.TourPlaceSearch;
import org.g9project4.publicData.tour.entities.QTourPlace;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewTourPlaceInfoService {
    @PersistenceContext
    private EntityManager em;

    private final TourPlaceRepository tourPlaceRepository;
    private final HttpServletRequest request;

    /**
     * 1. 태그들
     * 1-1 ContentType
     * 1-2 AreaCode
     *      1.구 소분류(시군구 코드)
     * 1-3 Category
     *      1. category1
     *      2. category2
     *      3. category3
     * 2. 검색 키워드
     * 3. 정렬 이름순, 최신순, 거리순, 인기순
     * @param search
     * @return
     */
    public ListData<TourPlace> getSearchedList(TourPlaceSearch search) {


        /* 검색 조건 처리 S */
        QTourPlace tourPlace = QTourPlace.tourPlace;
        BooleanBuilder andBuilder = new BooleanBuilder();


        if (search.getContentType() != null) {
            andBuilder.and(tourPlace.contentTypeId.eq(search.getContentType().getId()));
        }
        //검색 태그


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
                BooleanBuilder orBuilder = new BooleanBuilder();
                orBuilder.or(titleCond)
                        .or(addressCond);
                andBuilder.and(orBuilder);
            }

        }

        /* 검색 조건 처리 E */
        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        int offset = (page - 1) * limit;

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        int count = queryFactory.selectFrom(tourPlace)
                .where(andBuilder).fetch().size();
        JPAQuery<TourPlace> query = queryFactory.selectFrom(tourPlace)
                .offset(offset)
                .limit(limit)
                .where(andBuilder);
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
        // 거리 정보 주입
        Double latitude = item.getLatitude();
        Double longitude = item.getLongitude();
        Double distance = 0.0;
        if (latitude != null && longitude != null) {
            distance = Math.sqrt(Math.pow(latitude, 2) + Math.pow(longitude, 2));
        }
        item.setDistance(distance);
    }
}
