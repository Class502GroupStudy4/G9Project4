package org.g9project4.tourvisit;


import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.g9project4.publicData.tour.entities.QTourPlace;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.g9project4.tourvisit.services.TourPlaceRepositoryCustomImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@SpringJUnitConfig
public class TourPlaceRepositoryCustomImplTest {

    @Mock
    private JPAQueryFactory queryFactory;

    @Mock
    private TourPlaceRepository tourPlaceRepository;

    @InjectMocks
    private TourPlaceRepositoryCustomImpl tourPlaceRepositoryCustomImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetTourPlacesPoint() {
        // Arrange
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();

        // Mocking data
        TourPlace mockTourPlace = TourPlace.builder()
                .contentId(1L)
                .contentTypeId("someType")
                .title("해수욕장")
                .address("Some Address")
                .type1D1(100.0)
                .type1M1(200.0)
                .type1Y1(300.0)
                .type2D1(400.0)
                .type2M1(500.0)
                .type2Y1(600.0)
                .type3D1(700.0)
                .type3M1(800.0)
                .type3Y1(900.0)
                .build();

        List<TourPlace> mockTourPlaces = Arrays.asList(mockTourPlace);

        // Mock the queryFactory to return mock data
        // You should mock `JPAQuery` instead of `QueryResults`
        JPAQuery<TourPlace> jpaQueryMock = mock(JPAQuery.class);
        when(queryFactory.selectFrom(any(QTourPlace.class))).thenReturn(jpaQueryMock);
        when(jpaQueryMock.fetch()).thenReturn(mockTourPlaces);

        // Mock the repository to save and flush
        when(tourPlaceRepository.saveAllAndFlush(any())).thenReturn(mockTourPlaces);

        // Act
        List<TourPlace> result = tourPlaceRepositoryCustomImpl. getTourPlacesPoint("someType");

        // Assert
        verify(queryFactory, times(1)).selectFrom(any(QTourPlace.class));
        verify(jpaQueryMock, times(1)).fetch();
        verify(tourPlaceRepository, times(1)).saveAllAndFlush(any());
        // Add more assertions as needed
    }
}