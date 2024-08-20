package org.g9project4.publicData.tour.services;

import lombok.RequiredArgsConstructor;

import org.g9project4.file.exceptions.FileNotFoundException;
import org.g9project4.global.exceptions.DetailNotFoundException;
import org.g9project4.global.rests.gov.detailapi.DetailItem;
import org.g9project4.global.rests.gov.detailapi.DetailResult;
import org.g9project4.global.rests.gov.detailpetapi.DetailPetItem;
import org.g9project4.global.rests.gov.detailpetapi.DetailPetResult;
import org.g9project4.publicData.greentour.entities.GreenPlace;
import org.g9project4.publicData.greentour.repositories.GreenPlaceRepository;
import org.g9project4.publicData.tour.entities.PlaceDetail;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
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

    public PlaceDetail<DetailItem, DetailPetItem> getDetail(Long contentId) {
        String detailUrl = String.format("https://apis.data.go.kr/B551011/KorService1/detailCommon1?MobileOS=ETC&MobileApp=test&_type=json&contentId=%d&defaultYN=Y&firstImageYN=Y&&mapinfoYN=Y&overviewYN=Y&addrinfoYN=Y&serviceKey=%s", contentId, serviceKey);

        if (tourPlaceRepository.existsById(contentId)) {
            try {
                ResponseEntity<DetailResult> response = restTemplate.getForEntity(URI.create(detailUrl), DetailResult.class);
                if (response.getStatusCode().is2xxSuccessful() && response.getBody().getResponse().getHeader().getResultCode().equals("0000")) {
                    DetailItem detailResult = response.getBody().getResponse().getBody().getItems().getItem().get(0);
                    if (detailResult != null) {
                        return addPetDetail(detailResult);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new DetailNotFoundException();
            }
        } else if (greenPlaceRepository.existsById(contentId)) {
            try {
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
                return addPetDetail(detailItem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new DetailNotFoundException();
    }

    private PlaceDetail<DetailItem, DetailPetItem> addPetDetail(DetailItem detailItem) {
        try {
            Long contentId = detailItem.getContentid();
            String detailPetUrl = String.format("https://apis.data.go.kr/B551011/KorService1/detailPetTour1?serviceKey=%s&MobileOS=and&MobileApp=test&contentId=%d&_type=json", serviceKey, contentId);
            ResponseEntity<DetailPetResult> petResponse = null;
            restTemplate.getForEntity(URI.create(detailPetUrl), DetailPetResult.class);
            if (restTemplate.getForEntity(URI.create(detailPetUrl), DetailPetResult.class).getBody() == null) {
                return new PlaceDetail<>(detailItem, null);
            }
            petResponse = restTemplate.getForEntity(URI.create(detailPetUrl), DetailPetResult.class);
            if (petResponse.getBody() != null && petResponse.getStatusCode().is2xxSuccessful() &&
                    petResponse.getBody().getResponse().getHeader().getResultCode().equals("0000")) {
                DetailPetItem detailPetItem = petResponse.getBody().getResponse().getBody().getItems().getItem().get(0);
                if (detailPetItem != null) {
                    return new PlaceDetail<>(detailItem, detailPetItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PlaceDetail<>(detailItem, null);
    }
}
