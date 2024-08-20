package org.g9project4.tourvisit;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.g9project4.publicData.tour.entities.QTourPlace;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.g9project4.tourvisit.entities.QSigunguVisit;
import org.g9project4.tourvisit.entities.SigunguVisit;
import org.g9project4.tourvisit.services.VisitUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class VisitUpdateServiceTest {

    @Mock
    private JPAQueryFactory queryFactory;

    @Mock
    private TourPlaceRepository tourPlaceRepository;

    @InjectMocks
    private VisitUpdateService visitUpdateService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdate() {
        // Arrange
        QSigunguVisit sigunguVisit = QSigunguVisit.sigunguVisit;
        QTourPlace tourPlace = QTourPlace.tourPlace;

        // Mock data
        SigunguVisit sigunguVisitItem = new SigunguVisit();
        // Set mock values to sigunguVisitItem
        // ...

        List<SigunguVisit> sigunguVisitList = Arrays.asList(sigunguVisitItem);

        when(queryFactory.selectFrom(sigunguVisit).leftJoin(sigunguVisit.sidoVisit).fetchJoin().fetch())
                .thenReturn(sigunguVisitList);

        // Mock the result of tourPlaceRepository.findAll with an appropriate BooleanBuilder
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        // Set up the booleanBuilder with mock conditions
        // ...

        TourPlace tourPlaceItem = new TourPlace();
        // Set mock values to tourPlaceItem
        // ...

        List<TourPlace> tourPlaceList = Arrays.asList(tourPlaceItem);

        when(tourPlaceRepository.findAll(any(BooleanBuilder.class))).thenReturn(tourPlaceList);

        // Act
        visitUpdateService.update();

        // Assert
        // Verify that tourPlaceRepository.saveAllAndFlush() was called with the expected parameters
        verify(tourPlaceRepository, times(1)).saveAllAndFlush(tourPlaceList);
    }
}
