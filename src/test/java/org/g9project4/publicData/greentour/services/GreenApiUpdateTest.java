package org.g9project4.publicData.greentour.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GreenApiUpdateTest {
    @Autowired
    private GreenUpdateService service;
    @Test
    void test1(){
        service.greenUpdate();
    }
}
