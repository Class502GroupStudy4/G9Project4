package org.g9project4.publicData.tour.services;

import lombok.RequiredArgsConstructor;

import org.g9project4.global.rests.gov.areacodeapi.Code;
import org.g9project4.global.rests.gov.areacodeapi.CodeResult;
import org.g9project4.publicData.tour.entities.AreaCode;
import org.g9project4.publicData.tour.repositories.AreaCodeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaCodeUpdateService {
    private final RestTemplate restTemplate;
    private final AreaCodeRepository repository;
    private String sKey = "n5fRXDesflWpLyBNdcngUqy1VluCJc1uhJ0dNo4sNZJ3lkkaYkkzSSY9SMoZbZmY7/O8PURKNOFmsHrqUp2glA==";

    public void update() {
        String url = String.format("https://apis.data.go.kr/B551011/KorService1/areaCode1?MobileOS=AND&MobileApp=test&_type=json&serviceKey=%s", sKey);
        try {
            ResponseEntity<CodeResult> response = restTemplate.getForEntity(URI.create(url), CodeResult.class);
            if(response.getStatusCode().is2xxSuccessful()){
                List<Code> codes = response.getBody().getResponse().getCodeBody().getItems().getItem();
                for(Code code : codes){
                    AreaCode areaCode = new ModelMapper().map(code, AreaCode.class);
                    System.out.println(areaCode);
                    repository.saveAndFlush(areaCode);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
