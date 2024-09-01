package org.g9project4.publicData.tourvisit.services;

import lombok.RequiredArgsConstructor;
import org.g9project4.global.rests.gov.sigunguapi.ApiData;
import org.g9project4.global.rests.gov.sigunguapi.ApiResult;
import org.g9project4.publicData.tourvisit.entities.SigunguTable;
import org.g9project4.publicData.tourvisit.repositories.SigunguTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SigunguTableStatisticService {


    private final RestTemplate restTemplate;
    private final SigunguTableRepository sigunguTableRepository;

    private String sKey = "N8yeFJlK12h1MoldGswxyuc6R%2BmgDa7RcdKioykhYnC0eWRCYVumURmL0E10CgJ%2BWri%2B2Ln3xcXOs5pu%2BZ3sgA%3D%3D";
    //    @Scheduled(fixedRate = 1L, timeUnit = TimeUnit.DAYS)

    /**
     * API 데이터를 업데이트하고 저장된 SigunguTable 객체 리스트를 반환합니다.
     *
     * @return 저장된 SigunguTable 객체 리스트
     */
    public List<SigunguTable> update() {
        int totalPages = 25 ;  // totalPages 값을 적절히 할당

        List<SigunguTable> sigunguTables = new ArrayList<>();

        for (int i = 1; i <= totalPages; i++) {
            String url = String.format("https://api.odcloud.kr/api/15067699/v1/uddi:0c8ed5b5-30ff-4495-9b0d-d89f94d7308f?page=%d&perPage=10&returnType=JSON&serviceKey=%s", i, sKey);

            ApiResult result = restTemplate.getForObject(URI.create(url), ApiResult.class);
            if (result != null && result.getData() != null) {
                List<SigunguTable> tables = result.getData().stream()
                        .map(this::convertToSigunguTable)
                        .filter(table -> table.getSigunguCode2() != null)  // sigunguCode가 null이 아닌지 확인
                        .toList();

                sigunguTables.addAll(tables);
            }
        }

        //    Save all sigunguTables to the database and return the saved list
        return sigunguTableRepository.saveAllAndFlush(sigunguTables);
    }

    private SigunguTable convertToSigunguTable(ApiData apiData) {
        SigunguTable sigunguTable = new SigunguTable();
        sigunguTable.setSigunguName(apiData.getSigunguName());
        sigunguTable.setSigunguCode2(apiData.getSigunguCode2() != null ? apiData.getSigunguCode2().toString() : null);
        sigunguTable.setSidoCode(apiData.getSidoCode() != null ? apiData.getSidoCode().toString() : null);
        return sigunguTable;
    }


}