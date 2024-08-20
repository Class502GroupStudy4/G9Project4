package org.g9project4.tourvisit.services;

import lombok.RequiredArgsConstructor;
import org.g9project4.tourvisit.entities.SigunguTable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SigunguTableInfoService {


    private final  SigunguTableStatisticService apiService; // API 데이터를 가져오는 서비스

    public void updateTourPlaceSigunguCodes() {
        // SigunguTableStatisticService의 update 메서드를 호출하여 데이터를 가져옴
        List<SigunguTable> sigunguTableList = apiService.update();

    }
}
