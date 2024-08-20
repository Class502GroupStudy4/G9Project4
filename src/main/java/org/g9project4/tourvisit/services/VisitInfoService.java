package org.g9project4.tourvisit.services;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.g9project4.global.ListData;
import org.g9project4.global.Pagination;
import org.g9project4.publicData.tour.entities.QTourPlace;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;

import org.g9project4.tourvisit.controllers.VisitSearch;
import org.g9project4.tourvisit.repositories.SidoVisitRepository;
import org.g9project4.tourvisit.repositories.SigunguVisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.g9project4.publicData.tour.entities.QTourPlace.tourPlace;

@Transactional
@Service
@RequiredArgsConstructor
public class VisitInfoService {

    private final TourPlaceRepositoryCustomImpl tourPlaceRepositoryCustomImpl;
    @PersistenceContext
    private EntityManager em;

    private final JPAQueryFactory queryFactory;

    private final RestTemplate restTemplate;
    private final SidoVisitRepository sidorepository;
    private final SigunguVisitRepository sigungurepository;
    private final HttpServletRequest request;

    private final TourPlaceRepository repository;

    /**
     *
     */


    public ListData<TourPlace> getTotalList(VisitSearch  search) {
        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit < 1 ? 10 : limit;
        int offset = (page - 1) * limit;

        Pagination pagination = new Pagination(page, (int) repository.count(), 0, limit, request);
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QTourPlace tourPlace = QTourPlace.tourPlace;
        List<TourPlace> items = queryFactory.selectFrom(tourPlace)
                .orderBy(tourPlace.contentId.asc())
                .offset(offset)
                .limit(limit)
                .fetch();
        return new ListData<>(items, pagination);
    }




    public ListData<TourPlace> getSearchedList(VisitSearch search) {
        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit < 1 ? 10 : limit;
        int offset = (page - 1) * limit;

       //  contentTypeId를 Long으로 직접 사용
           String contentTypeId = search.getContentTypeId();

        int count = queryFactory.selectFrom(tourPlace)
                .where().fetch().size();


     //    TourPlaceQuery에서 데이터를 조회
        List<TourPlace> places = tourPlaceRepositoryCustomImpl.getTourPlacesPoint(contentTypeId);

        // ListData 객체로 변환
        Pagination pagination = new Pagination(page, count, 0, limit, request); // Pagination 객체를 필요에 따라 설정
       return new ListData<>(places, pagination);





    }
}