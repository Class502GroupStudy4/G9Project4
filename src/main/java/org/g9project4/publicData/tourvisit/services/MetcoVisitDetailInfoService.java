package org.g9project4.publicData.tourvisit.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.g9project4.global.rests.gov.detailapi.DetailItem;
import org.g9project4.global.rests.gov.detailapi.DetailResult;
import org.g9project4.publicData.tourvisit.entities.MetcoVisitId;
import org.g9project4.publicData.tourvisit.repositories.MetcoVisitRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class MetcoVisitDetailInfoService {

    private final RestTemplate restTemplate;
    private final MetcoVisitRepository repository;
    private final String serviceKey = "RtrIIdYjcb3IXn1a/zF7itGWY5ZFS3IEj85ohFx/snuKG9hYABL5Tn8jEgCEaCw6uEIHvUz30yF4n0GGP6bVIA=="; //경미계정

    public DetailItem getDetail(MetcoVisitId locId) {
        // MetcoVisitId에서 필드 추출
        Long signguCode = locId.getAreaCode();

        LocalDate baseYmd = LocalDate.now(); // 또는 원하는 날짜로 초기화
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedBaseYmd = baseYmd.format(formatter);

        // URL을 필드 값으로 구성
        String url = String.format("https://apis.data.go.kr/B551011/DataLabService/metcoRegnVisitrDDList?MobileOS=AND&MobileApp=test&signguCode=%d&baseYmd=%s&serviceKey=%s&startYmd=20220701&endYmd=20240416&_type=json",
                signguCode,  formattedBaseYmd, serviceKey);

        try {
            ResponseEntity<DetailResult> response = restTemplate.getForEntity(URI.create(url), DetailResult.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody().getResponse().getHeader().getResultCode().equals("0000")) {
                List<DetailItem> items = response.getBody().getResponse().getBody().getItems().getItem();
                if (items != null && !items.isEmpty()) {
                    return items.get(0); // 첫 번째 아이템 반환
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 발생 시 적절한 로깅 또는 사용자 정의 예외 처리 고려
        }
        return null;
    }
    
}
