package org.g9project4.publicData.tour.services;

import lombok.RequiredArgsConstructor;

import org.g9project4.file.exceptions.FileNotFoundException;
import org.g9project4.global.exceptions.DetailNotFoundException;
import org.g9project4.global.rests.gov.detailapi.DetailItem;
import org.g9project4.global.rests.gov.detailapi.DetailResult;
import org.g9project4.publicData.greentour.entities.GreenPlace;
import org.g9project4.publicData.greentour.repositories.GreenPlaceRepository;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TourDetailInfoService {
    private final RestTemplate restTemplate;
    private final TourPlaceRepository tourPlaceRepository;
    private final GreenPlaceRepository greenPlaceRepository;
    private String serviceKey = "n5fRXDesflWpLyBNdcngUqy1VluCJc1uhJ0dNo4sNZJ3lkkaYkkzSSY9SMoZbZmY7/O8PURKNOFmsHrqUp2glA==";

    public DetailItem getDetail(Long contentId) {
        String url = String.format("https://apis.data.go.kr/B551011/KorService1/detailCommon1?MobileOS=ETC&MobileApp=test&_type=json&contentId=%d&defaultYN=Y&firstImageYN=Y&&mapinfoYN=Y&overviewYN=Y&addrinfoYN=Y&serviceKey=%s", contentId, serviceKey);
        if (tourPlaceRepository.existsById(contentId)) {
            try {
                ResponseEntity<DetailResult> response = restTemplate.getForEntity(URI.create(url), DetailResult.class);
                if (response.getStatusCode().is2xxSuccessful() && response.getBody().getResponse().getHeader().getResultCode().equals("0000")) {
                    DetailItem detailResult = response.getBody().getResponse().getBody().getItems().getItem().get(0);
                    if (detailResult != null) {
                        return detailResult;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new DetailNotFoundException();
            }
        } else if (greenPlaceRepository.existsById(contentId)) {
            GreenPlace greenPlace = greenPlaceRepository.findById(contentId).orElseThrow(DetailNotFoundException::new);
            DetailItem detailItem = DetailItem.builder()
                    .title(greenPlace.getTitle())
                    .homepage(null)
                    .tel(greenPlace.getTel())
                    .firstimage(greenPlace.getFirstImage())
                    .overview(greenPlace.getSummary())
                    .addr1(greenPlace.getAddress())
                    .mapx(null)
                    .mapy(null)
                    .build();
            return detailItem;
        }
        throw new DetailNotFoundException();
    }
}
