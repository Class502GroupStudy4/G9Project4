package org.g9project4.publicData.tour.services;

import lombok.RequiredArgsConstructor;

import org.g9project4.global.rests.gov.areacodeapi.Code;
import org.g9project4.global.rests.gov.areacodeapi.CodeResult;
import org.g9project4.global.rests.gov.sigunguapi.SigunguItem;
import org.g9project4.global.rests.gov.sigunguapi.SigunguResponse;
import org.g9project4.global.rests.gov.sigunguapi.SigunguResult;
import org.g9project4.publicData.tour.entities.AreaCode;
import org.g9project4.publicData.tour.entities.SigunguCode;
import org.g9project4.publicData.tour.repositories.AreaCodeRepository;
import org.g9project4.publicData.tour.repositories.SigunguCodeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaCodeUpdateService {
    private final RestTemplate restTemplate;
    private final AreaCodeRepository areaCodeRepository;
    private final SigunguCodeRepository sigunguCodeRepository;
    private String sKey = "n5fRXDesflWpLyBNdcngUqy1VluCJc1uhJ0dNo4sNZJ3lkkaYkkzSSY9SMoZbZmY7/O8PURKNOFmsHrqUp2glA==";

    public void update() {
        String url = String.format("https://apis.data.go.kr/B551011/KorService1/areaCode1?numOfRows=50&MobileOS=AND&MobileApp=test&_type=json&serviceKey=%s", sKey);

        try {
            ResponseEntity<CodeResult> response = restTemplate.getForEntity(URI.create(url), CodeResult.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                List<Code> codes = response.getBody().getResponse().getCodeBody().getItems().getItem();
                for (Code code : codes) {
                    AreaCode areaCode = AreaCode.builder()
                            .areaCode(code.getCode())
                            .name(code.getName())
                            .build();
                    areaCodeRepository.saveAndFlush(areaCode);
                }
                List<AreaCode> areaCodeList = areaCodeRepository.findAll();
                for (AreaCode areaCode : areaCodeList) {
                    String url2 = String.format("https://apis.data.go.kr/B551011/KorService1/areaCode1?numOfRows=50&MobileOS=and&MobileApp=test&areaCode=%s&_type=json&serviceKey=%s", areaCode.getAreaCode(), sKey);
                    ResponseEntity<SigunguResult> sigunguResponse = restTemplate.getForEntity(URI.create(url2), SigunguResult.class);
                    if (response.getStatusCode().is2xxSuccessful()) {
                        List<SigunguItem> sigungus = sigunguResponse.getBody().getResponse().getBody().getItems().getItem();
                        for (SigunguItem code : sigungus) {
                            SigunguCode sigunguCode = SigunguCode.builder()
                                    .areaCode(areaCode.getAreaCode())
                                    .sigunguCode(code.getCode())
                                    .name(code.getName())
                                    .build();
                            sigunguCodeRepository.saveAndFlush(sigunguCode);
                        }
                    }
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
