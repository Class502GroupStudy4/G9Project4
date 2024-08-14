package org.g9project4.publicData.greentour.services;

import lombok.RequiredArgsConstructor;
import org.g9project4.global.rests.gov.greenapi.GreenItem;
import org.g9project4.global.rests.gov.greenapi.GreenResult;
import org.g9project4.publicData.greentour.entities.GreenPlace;
import org.g9project4.publicData.greentour.repositories.GreenPlaceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GreenUpdateService {
    private final RestTemplate restTemplate;
    private final GreenPlaceRepository greenPlaceRepository;
    private String serviceKey = "n5fRXDesflWpLyBNdcngUqy1VluCJc1uhJ0dNo4sNZJ3lkkaYkkzSSY9SMoZbZmY7/O8PURKNOFmsHrqUp2glA==";

    public void greenUpdate() {
        for (int i = 0; i < 10; i++) {
            String url = String.format("https://apis.data.go.kr/B551011/GreenTourService1/areaBasedList1?numOfRows=30&pageNo=%d&MobileOS=AND&MobileApp=test&_type=json&serviceKey=%s", i, serviceKey);
            ResponseEntity<GreenResult> response = restTemplate.getForEntity(url, GreenResult.class);
            try {
                response = restTemplate.getForEntity(URI.create(url), GreenResult.class);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            if (response.getStatusCode().is2xxSuccessful()) {
                List<GreenItem> items = response.getBody().getResponse().getBody().getItems().getItem();
                for (GreenItem item : items) {
                    try {
                        GreenPlace greenPlace = GreenPlace.builder()
                                .addr(item.getAddr())
                                .areacode(item.getAreacode())
                                .contentId(item.getContentid())
                                .mainImage(item.getMainimage())
                                .cpyrhtDivCd(item.getCpyrhtDivCd())
                                .modifiedtime(item.getModifiedtime())
                                .sigugunCode(item.getSigungucode())
                                .subTitle(item.getSubtitle())
                                .summary(item.getSummary())
                                .tel(item.getTel())
                                .telName(item.getTelname())
                                .title(item.getTitle())
                                .build();
                        greenPlaceRepository.saveAndFlush(greenPlace);
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }
    }
}
