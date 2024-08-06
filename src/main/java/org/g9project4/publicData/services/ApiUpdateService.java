package org.g9project4.publicData.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ApiUpdateService {
    private final RestTemplate restTemplate;

    private String serviceKey = "n5fRXDesflWpLyBNdcngUqy1VluCJc1uhJ0dNo4sNZJ3lkkaYkkzSSY9SMoZbZmY7/O8PURKNOFmsHrqUp2glA==";

    public void update() {//데이터 베이스 업데이트
        for (int i = 0; i < 100; i++) {
            String url = String.format("https://apis.data.go.kr/B551011/KorService1/areaBasedList1?MobileOS=AND&MobileApp=test&numOfRows=1000&pageNo=%d&serviceKey=%s&_type=json",i,serviceKey);
        }
    }
}
