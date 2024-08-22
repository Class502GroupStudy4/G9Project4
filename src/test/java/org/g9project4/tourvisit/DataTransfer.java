package org.g9project4.tourvisit;


import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.g9project4.publicData.tour.entities.QTourPlace;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.g9project4.publicData.tour.services.ApiUpdateService;
//import org.g9project4.tourvisit.services.TourPlaceRepositoryCustomImpl;
import org.g9project4.tourvisit.services.*;
//import org.g9project4.tourvisit.services.SigunguTableStatisticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
//@ActiveProfiles("test")
public class DataTransfer {

    @Autowired
    private TourPlaceRepository tourPlaceRepository;

    @Autowired
    private SidoVisitStatisticService service;

    @Autowired
    private SigunguVisitStatisticService service2;

    @Autowired
    private ApiUpdateService apiUpdateService;

    @Autowired
    private VisitUpdateService visitUpdateService;

    @Autowired
    private SigunguTableStatisticService tableService;


    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private TourplacePointService pointService;

    @PersistenceContext
    private EntityManager em;


    @Test
    void test1() {
        service.updateSidoVisit("1D");
        service.updateSidoVisit("1W");
        service.updateSidoVisit("1M");
        service.updateSidoVisit("3M");
        service.updateSidoVisit("6M");
        service.updateSidoVisit("1Y");
    }

    @Test
    void test2() {
        service2.updateVisit("1D");
        service2.updateVisit("1W");
        service2.updateVisit("1M");
        service2.updateVisit("3M");
        service2.updateVisit("6M");
        service2.updateVisit("1Y");
    }

    @Test
    void test3() {

        visitUpdateService.update();
    }

    @Test
    void test4() {
        apiUpdateService.update();

    }


    @Test
    void test5() {
        tableService.update();
    }




    @Test
    void test7() {

        pointService.update();

    }
}



// ContentType별로 내림차순 정렬된 리스트를 가져옵니다.
//List<QTourPlace> sortedTourPlaces = impl.getContenttypeIdPointList();
//        System.out.println("Sorted TourPlaces by placePointValue:");
//        sortedTourPlaces.forEach(System.out::println);



