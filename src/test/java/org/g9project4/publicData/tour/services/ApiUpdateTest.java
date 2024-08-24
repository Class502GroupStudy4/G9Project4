package org.g9project4.publicData.tour.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ApiUpdateTest {
    @Autowired
    private ApiUpdateService apiUpdateService;

    private String sKey = "n5fRXDesflWpLyBNdcngUqy1VluCJc1uhJ0dNo4sNZJ3lkkaYkkzSSY9SMoZbZmY7/O8PURKNOFmsHrqUp2glA==";
    @Test
    public void test() {
        apiUpdateService.update(sKey);
    }

    @Test
    public void test2() {
        apiUpdateService.areaCodeUpdate(sKey);
    }
    @Test
    public void test3(){
        apiUpdateService.categoryUpdate(sKey);
    }
}
