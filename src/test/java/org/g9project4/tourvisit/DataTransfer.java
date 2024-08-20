package org.g9project4.tourvisit;


import jakarta.transaction.Transactional;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.g9project4.publicData.tour.services.ApiUpdateService;
import org.g9project4.tourvisit.services.TourPlaceRepositoryCustomImpl;
import org.g9project4.tourvisit.services.SidoVisitStatisticService;
import org.g9project4.tourvisit.services.SigunguTableStatisticService;
import org.g9project4.tourvisit.services.SigunguVisitStatisticService;
import org.g9project4.tourvisit.services.VisitUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Transactional
@SpringBootTest
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
    private TourPlaceRepositoryCustomImpl impl;

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
     //   apiUpdateService.update();
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
   void test6() {
        // Call the method to update SigunguCode2


   impl.updateSigunguCode2();

    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExtractSigunguNameFromAddress() {
        // Test cases with various address formats
        assertEquals("경기도광주시", impl.extractSigunguNameFromAddress("경기도 광주시 이배재로 493 (목현동)"));
        assertEquals("서울특별시강남구", impl.extractSigunguNameFromAddress("서울특별시 강남구 테헤란로 123"));
        assertEquals("부산광역시해운대구", impl.extractSigunguNameFromAddress("부산광역시 해운대구 우동 456"));
        // Add more test cases as needed
    }
}


