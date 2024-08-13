package org.g9project4.publicData.tour.services;

import org.g9project4.publicData.tourvisit.services.LocgoVisitUpdateService;
import org.g9project4.publicData.tourvisit.services.MetcoVisitUpdateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ApiUpdateTest {
    @Autowired
    private ApiUpdateService apiUpdateService;

    @Autowired
    private LocgoVisitUpdateService locupdateService;

    @Autowired
    private MetcoVisitUpdateService metupdateService;

    @Test
    public void test() {
        apiUpdateService.update();
    }

    @Test
    void test2() {
        locupdateService.update();
    }

    @Test
    void test3() {
        metupdateService.update();
    }



}
