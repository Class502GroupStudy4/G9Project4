package org.g9project4.publicData.tourvisit.services;


import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.g9project4.global.ListData;
import org.g9project4.global.Pagination;

import org.g9project4.global.rests.gov.metcoVisit.MetcoVisitItem;
import org.g9project4.global.rests.gov.metcoVisit.MetcoVisitResult;
import org.g9project4.publicData.tourvisit.controllers.MetcoVisitSearch;

import org.g9project4.publicData.tourvisit.entities.MetcoVisit;
import org.g9project4.publicData.tourvisit.entities.QMetcoVisit;
import org.g9project4.publicData.tourvisit.repositories.MetcoVisitRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class MetcoVisitInfoSerivce {

    @PersistenceContext
    private EntityManager em;

    private final RestTemplate restTemplate;
    private final MetcoVisitRepository repository;
    private String serviceKey = "RtrIIdYjcb3IXn1a/zF7itGWY5ZFS3IEj85ohFx/snuKG9hYABL5Tn8jEgCEaCw6uEIHvUz30yF4n0GGP6bVIA=="; //경미계정

    /**
     *
     */
    public List<MetcoVisit> getLocBasedList(MetcoVisitSearch search) {

        int areaCode = search.getAreaCode();
        String touDivNm = search.getTouDivNm();
        LocalDate baseYmd = search.getBaseYmd();
String areaNm = search.getAreaNm();
        int radius = search.getRadius();
        String url = String.format("https://apis.data.go.kr/B551011/DataLabService/metcoRegnVisitrDDList?MobileOS=AND&MobileApp=test&areaCode=%d&touDivNm=%s&areaNm=%s&radius=%d&serviceKey=%s&startYmd=20220701&endYmd=20240416&_type=json", areaCode, touDivNm, areaNm,  radius, serviceKey);
        try {
            ResponseEntity<MetcoVisitResult> response = restTemplate.getForEntity(URI.create(url), MetcoVisitResult.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody().getResponse().getHeader().getResultCode().equals("0000")) {

                List<Long> areaCodes = response.getBody().getResponse().getBody().getItems().getItem().stream()
                        .map(MetcoVisitItem::getAreaCode).collect(Collectors.toList());
                List<String> touDivNms = response.getBody().getResponse().getBody().getItems().getItem().stream()
                        .map(MetcoVisitItem::getTouDivNm).collect(Collectors.toList());


                if (!areaCodes.isEmpty() && !touDivNms.isEmpty()) {
                    QMetcoVisit metcoVisit = QMetcoVisit.metcoVisit;
                    List<MetcoVisit> items = (List<MetcoVisit>) repository.findAll(
                            metcoVisit.areaCode.in(areaCodes)
                                    .and(metcoVisit.touDivNm.in(touDivNms)),
                            Sort.by(Sort.Direction.ASC, "areaCode", "touDivNm")
                    );

                    return items;
                } // endif
            } // endif
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public ListData<MetcoVisit> getTotalList(MetcoVisitSearch search) {
        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit < 1 ? 10 : limit;
        int offset = (page - 1) * limit;

        Pagination pagination = new Pagination(page, (int) repository.count(), 0, limit);
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMetcoVisit metcoVisit = QMetcoVisit.metcoVisit;

        // Order by the primary key fields
        List<MetcoVisit> items = queryFactory.selectFrom(metcoVisit)
                .orderBy(metcoVisit.areaCode.asc(), metcoVisit.touDivNm.asc()) // Sort by composite key fields
                .offset(offset)
                .limit(limit)
                .fetch();

        return new ListData<>(items, pagination);
    }

    public ListData<MetcoVisit> getSearchedList(MetcoVisitSearch search) {
        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit < 1 ? 10 : limit;
        int offset = (page - 1) * limit;

        Pagination pagination = new Pagination(page, (int) repository.count(), 0, limit);
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMetcoVisit metcoVisit = QMetcoVisit.metcoVisit;

        // Filter by areaNm and apply pagination and ordering
        List<MetcoVisit> items = queryFactory.selectFrom(metcoVisit)
                .where(metcoVisit.areaNm.eq(search.getAreaNm())) // Assuming `areaNm` is a String
                .orderBy(metcoVisit.areaCode.asc(), metcoVisit.touDivNm.asc()) // Sort by composite key fields
                .offset(offset)
                .limit(limit)
                .fetch();

        return new ListData<>(items, pagination);
    }

}
