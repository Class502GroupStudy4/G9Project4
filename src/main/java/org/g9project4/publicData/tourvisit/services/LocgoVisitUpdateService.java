package org.g9project4.publicData.tourvisit.services;

import lombok.RequiredArgsConstructor;
import org.g9project4.global.rests.gov.locgovisit.LocgoVisitItem;
import org.g9project4.global.rests.gov.locgovisit.LocgoVisitResult;
import org.g9project4.publicData.tourvisit.entities.LocgoVisit;
import org.g9project4.publicData.tourvisit.repositories.LocgoVisitRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class LocgoVisitUpdateService {

    private final RestTemplate restTemplate;
    private final LocgoVisitRepository locgoVisitRepository;



    private String serviceKey = "RtrIIdYjcb3IXn1a/zF7itGWY5ZFS3IEj85ohFx/snuKG9hYABL5Tn8jEgCEaCw6uEIHvUz30yF4n0GGP6bVIA==";  //경미계정

    //&pageNo=%d&serviceKey=%s
    public void update() {
        for (int i = 0; i < 100; i++) {

            String url = String.format("https://apis.data.go.kr/B551011/DataLabService/locgoRegnVisitrDDList?MobileOS=AND&MobileApp=test&pageNo=%d&serviceKey=%s&startYmd=20220701&endYmd=20240416&_type=json", i, serviceKey);

            ResponseEntity<LocgoVisitResult> response = null;
            try {
                response = restTemplate.getForEntity(URI.create(url), LocgoVisitResult.class);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println(response.getBody());
                List<LocgoVisitItem> items = response.getBody().getResponse().getBody().getItems().getItem();
                for (LocgoVisitItem item : items) {
                    try {
                       // System.out.println(item);
                        LocgoVisit locgoVisit = LocgoVisit.builder()
                                .signguCode(item.getSignguCode())
                                .baseYmd(item.getBaseYmd())
                                .signguNm(item.getSignguNm())
                                .daywkDivCd(item.getDaywkDivCd())
                                .daywkDivNm(item.getDaywkDivNm())
                                .touDivCd(item.getTouDivCd())
                                .touDivNm(item.getTouDivNm())
                                .touNum(item.getTouNum())
                                .build();
                        System.out.println(locgoVisit);
                        locgoVisitRepository.saveAndFlush(locgoVisit);
                    } catch (Exception e) {//예외 발생하면 이미 등록된 여행지
                        e.printStackTrace();
                        continue; // 이미 등록된 경우 무시하고 다음으로 넘어감
                    }
                }
            } else {
                break; // 성공적인 응답이 아닌 경우 루프 종료

            }
        }
    }
}





