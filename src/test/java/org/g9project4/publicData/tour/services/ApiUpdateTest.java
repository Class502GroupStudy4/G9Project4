package org.g9project4.publicData.tour.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ApiUpdateTest {
    @Autowired
    private ApiUpdateService apiUpdateService;
    @Autowired
    private AreaCodeUpdateService areaCodeUpdateService;

    @Test
    public void test() {
        apiUpdateService.update();
    }

    @Test
    public void test2() {
        areaCodeUpdateService.update();
    }
}
