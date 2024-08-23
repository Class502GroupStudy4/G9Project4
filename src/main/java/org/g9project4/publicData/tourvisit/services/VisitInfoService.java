package org.g9project4.publicData.tourvisit.services;

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
import org.g9project4.publicData.tour.entities.QTourPlace;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;


import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

//
//@Service
//@RequiredArgsConstructor
//public class VisitInfoService {
//
//    @PersistenceContext
//    private EntityManager em;
//
//    private final RestTemplate restTemplate;
//    private final TourPlaceRepository repository;
//    private String serviceKey = "n5fRXDesflWpLyBNdcngUqy1VluCJc1uhJ0dNo4sNZJ3lkkaYkkzSSY9SMoZbZmY7/O8PURKNOFmsHrqUp2glA==";
//    private final HttpServletRequest request;
//
//    public ListData<TourPlace> getContenttypeIdPointList(VisitSearch search) {
//
//        int page = Math.max(search.getPage(), 1);
//        int limit = search.getLimit();
//        limit = limit < 1 ? 10 : limit;
//        int offset = (page - 1) * limit;
//
//        /* 검색 조건 처리 S */
//        QTourPlace tourPlace = QTourPlace.tourPlace;
//        BooleanBuilder andBuilder = new BooleanBuilder();
//
//        String sopt = search.getSopt();
//        String skey = search.getSkey();
//
//        sopt = StringUtils.hasText(sopt) ? sopt.toUpperCase() : "ALL";
//
//        if (StringUtils.hasText(skey)) {
//            skey = skey.trim();
//
//            BooleanExpression titleCond = tourPlace.title.contains(skey); // 제목 - subject LIKE '%skey%';
//            BooleanExpression addressCond = tourPlace.address.contains(skey); // 내용 - content LIKE '%skey%';
//
//            if (sopt.equals("TITLE")) { // 여행지 이름
//
//                andBuilder.and(titleCond);
//
//            } else if (sopt.equals("ADDRESS")) { // 주소
//
//                andBuilder.and(addressCond);
//
//            } else if (sopt.equals("TITLE_ADDRESS")) { // 제목 + 내용
//
//                BooleanBuilder orBuilder = new BooleanBuilder();
//                orBuilder.or(titleCond)
//                        .or(addressCond);
//
//                andBuilder.and(orBuilder);
//
//            }
//
//        }
//
//        /* 검색 조건 처리 E */
//
//        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
//        int count = queryFactory.selectFrom(tourPlace)
//                .where(andBuilder).fetch().size();
//        JPAQuery<TourPlace> query = queryFactory.selectFrom(tourPlace)
//                .orderBy(tourPlace.contentId.asc())
//                .offset(offset)
//                .limit(limit)
//                .where(andBuilder);
//        List<TourPlace> items = query.fetch();
//        Pagination pagination = new Pagination(page, count, 0, limit, request);
//        return new ListData<>(items, pagination);
//    }
//
//}
//
//
