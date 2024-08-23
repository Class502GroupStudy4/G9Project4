package org.g9project4.tourvisit;


import jakarta.transaction.Transactional;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.g9project4.publicData.tourvisit.services.TourplacePointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
@Transactional
public class TourplacePointServiceTest {

    @Autowired
    private TourplacePointService tourplacePointService;

    @Autowired
    private TourPlaceRepository tourPlaceRepository;

    @BeforeEach
    public void setup() {
        List<TourPlace> tourPlaces = createTestTourPlaces(10);
        tourPlaceRepository.saveAll(tourPlaces);
        tourPlaceRepository.flush();
    }

    @Test
    public void testUpdateFirst10TourPlaces() {
        // Act
        tourplacePointService.update();

        // Assert
        List<TourPlace> updatedTourPlaces = tourPlaceRepository.findAll().subList(0, 10);

        for (TourPlace item : updatedTourPlaces) {
            System.out.println("Updated TourPlace: " + item.getTitle() + " - PlacePointValue: " + item.getPlacePointValue());
            assertThat(item.getPlacePointValue()).isGreaterThanOrEqualTo(0);
        }
    }

    private List<TourPlace> createTestTourPlaces(int count) {
        return IntStream.range(0, count).mapToObj(i -> {
            TourPlace tourPlace = new TourPlace();
            tourPlace.setContentId((long) (i + 1)); // contentId를 수동으로 설정
            tourPlace.setTitle("Test Place " + (i + 1));
            tourPlace.setType1D1(100.0);
            tourPlace.setType2D1(100.0);
            tourPlace.setType3D1(100.0);
            tourPlace.setType1W1(100.0);
            tourPlace.setType2W1(100.0);
            tourPlace.setType3W1(100.0);
            tourPlace.setType1M1(100.0);
            tourPlace.setType2M1(100.0);
            tourPlace.setType3M1(100.0);
            tourPlace.setType1M3(100.0);
            tourPlace.setType2M3(100.0);
            tourPlace.setType3M3(100.0);
            tourPlace.setType1M6(100.0);
            tourPlace.setType2M6(100.0);
            tourPlace.setType3M6(100.0);
            tourPlace.setType1Y1(100.0);
            tourPlace.setType2Y1(100.0);
            tourPlace.setType3Y1(100.0);
            return tourPlace;
        }).collect(Collectors.toList());
    }
}