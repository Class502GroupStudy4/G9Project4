package org.g9project4.publicData.tourvisit.services;


import lombok.RequiredArgsConstructor;
import org.g9project4.global.rests.gov.metcoVisit.MetcoVisitItem;
import org.g9project4.global.rests.gov.metcoVisit.MetcoVisitResult;
import org.g9project4.publicData.tourvisit.entities.MetcoVisit;
import org.g9project4.publicData.tourvisit.repositories.MetcoVisitRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class MetcoVisitUpdateService {

    private final RestTemplate restTemplate;
    private final MetcoVisitRepository metcoVisitRepository;

    private String serviceKey = "RtrIIdYjcb3IXn1a/zF7itGWY5ZFS3IEj85ohFx/snuKG9hYABL5Tn8jEgCEaCw6uEIHvUz30yF4n0GGP6bVIA==";  // 경미 계정

    public void update() {
        for (int i = 0; i < 100; i++) {

            String url = String.format("https://apis.data.go.kr/B551011/DataLabService/metcoRegnVisitrDDList?MobileOS=AND&MobileApp=test&pageNo=%d&serviceKey=%s&startYmd=20220701&endYmd=20240416&_type=json", i, serviceKey);

            ResponseEntity<MetcoVisitResult> response = null;
            try {
                response = restTemplate.getForEntity(URI.create(url), MetcoVisitResult.class);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            if (response.getStatusCode().is2xxSuccessful()) {
                List<MetcoVisitItem> items = response.getBody().getResponse().getBody().getItems().getItem();
                for (MetcoVisitItem item : items) {
                    try {
                        if (item.getAreaCode() != null) {
                            MetcoVisit metcoVisit = MetcoVisit.builder()
                                    .areaCode(item.getAreaCode())
                                    .baseYmd(item.getBaseYmd())
                                    .areaNm(item.getAreaNm())
                                    .daywkDivCd(item.getDaywkDivCd())
                                    .daywkDivNm(item.getDaywkDivNm())
                                    .touDivCd(item.getTouDivCd())
                                    .touDivNm(item.getTouDivNm())
                                    .touNum(item.getTouNum())
                                    .build();

                            metcoVisitRepository.saveAndFlush(metcoVisit);
                        } else {
                            System.out.println("areaCode가 NULL이므로 삽입하지 않습니다: " + item);
                        }
                    } catch (Exception e) {
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
