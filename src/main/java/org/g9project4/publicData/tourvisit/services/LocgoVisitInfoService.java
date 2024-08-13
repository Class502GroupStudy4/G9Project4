package org.g9project4.publicData.tourvisit.services;


import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.g9project4.global.ListData;
import org.g9project4.global.Pagination;
import org.g9project4.global.rests.gov.locgovisit.LocgoVisitItem;
import org.g9project4.global.rests.gov.locgovisit.LocgoVisitResult;
import org.g9project4.publicData.tourvisit.controllers.LocgoVisitSearch;
import org.g9project4.publicData.tourvisit.entities.LocgoVisit;
import org.g9project4.publicData.tourvisit.entities.QLocgoVisit;
import org.g9project4.publicData.tourvisit.repositories.LocgoVisitRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
@Transactional
@Service
@RequiredArgsConstructor
public class LocgoVisitInfoService {

    @PersistenceContext
    private EntityManager em;

    private final RestTemplate restTemplate;
    private final LocgoVisitRepository repository;
    private String serviceKey = "RtrIIdYjcb3IXn1a/zF7itGWY5ZFS3IEj85ohFx/snuKG9hYABL5Tn8jEgCEaCw6uEIHvUz30yF4n0GGP6bVIA=="; //경미계정

    /**
     *
     */
    public List<LocgoVisit> getLocBasedList(LocgoVisitSearch search) {

        int signguCode = search.getSignguCode();
        String touDivNm = search.getTouDivNm();
        String signguNm =search.getSignguNm();

        int radius = search.getRadius();
        String url = String.format("https://apis.data.go.kr/B551011/DataLabService/locgoRegnVisitrDDList?MobileOS=AND&MobileApp=test&signguCode=%d&touDivNm=%s&signguNm=%s&radius=%d&serviceKey=%s&startYmd=20220701&endYmd=20240416&_type=json", signguCode, touDivNm, signguNm,  radius, serviceKey);
        try {
            ResponseEntity<LocgoVisitResult> response = restTemplate.getForEntity(URI.create(url), LocgoVisitResult.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody().getResponse().getHeader().getResultCode().equals("0000")) {

                List<Long> signguCodes = response.getBody().getResponse().getBody().getItems().getItem().stream()
                        .map(LocgoVisitItem::getSignguCode).collect(Collectors.toList());
                List<String> touDivNms = response.getBody().getResponse().getBody().getItems().getItem().stream()
                        .map(LocgoVisitItem::getTouDivNm).collect(Collectors.toList());


                if (!signguCodes.isEmpty() && !touDivNms.isEmpty()) {
                    QLocgoVisit locgoVisit = QLocgoVisit.locgoVisit;
                    List<LocgoVisit> items = (List<LocgoVisit>) repository.findAll(
                            locgoVisit.signguCode.in(signguCodes)
                                    .and(locgoVisit.touDivNm.in(touDivNms)),
                            Sort.by(Sort.Direction.ASC, "signguCode", "touDivNm")
                    );

                    return items;
                } // endif
            } // endif
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public ListData<LocgoVisit> getTotalList(LocgoVisitSearch search) {
        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit < 1 ? 10 : limit;
        int offset = (page - 1) * limit;

        Pagination pagination = new Pagination(page, (int) repository.count(), 0, limit);
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QLocgoVisit locgoVisit = QLocgoVisit.locgoVisit;

        // Order by the primary key fields
        List<LocgoVisit> items = queryFactory.selectFrom(locgoVisit)
                .orderBy(locgoVisit.signguCode.asc(), locgoVisit.touDivNm.asc()) // Sort by composite key fields
                .offset(offset)
                .limit(limit)
                .fetch();

        return new ListData<>(items, pagination);
    }

    public ListData<LocgoVisit> getSearchedList(LocgoVisitSearch search) {
        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit < 1 ? 10 : limit;
        int offset = (page - 1) * limit;

        Pagination pagination = new Pagination(page, (int) repository.count(), 0, limit);
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QLocgoVisit locgoVisit = QLocgoVisit.locgoVisit;

        // Filter by signguNm and apply pagination and ordering
        List<LocgoVisit> items = queryFactory.selectFrom(locgoVisit)
                .where(locgoVisit.signguNm.eq(search.getSignguNm())) // Assuming `signguNm` is a String
                .orderBy(locgoVisit.signguCode.asc(), locgoVisit.touDivNm.asc()) // Sort by composite key fields
                .offset(offset)
                .limit(limit)
                .fetch();

        return new ListData<>(items, pagination);
    }

}
