package org.g9project4.tourvisit.services;

import lombok.RequiredArgsConstructor;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourplacePointService {

    private final TourPlaceRepositoryCustomImpl tourPlaceRepositoryCustomImpl;


    /**
     * 주어진 contentTypeId로 투어 장소를 찾습니다.
     *
     * @param contentTypeId 콘텐츠 유형 ID
     * @return 찾은 투어 장소 목록
     */
    public List<TourPlace> getTourPlacesByContentType(String contentTypeId) {
        return tourPlaceRepositoryCustomImpl.getTourPlacesPoint(contentTypeId);
    }
}
