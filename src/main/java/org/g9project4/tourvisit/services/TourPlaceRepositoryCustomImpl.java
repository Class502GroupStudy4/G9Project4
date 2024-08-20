package org.g9project4.tourvisit.services;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.g9project4.publicData.tour.entities.QTourPlace;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.g9project4.tourvisit.controllers.TourPlaceRepositorycustom;
import org.g9project4.tourvisit.entities.QSidoVisit;
import org.g9project4.tourvisit.entities.QSigunguTable;
import org.g9project4.tourvisit.entities.SidoVisit;
import org.g9project4.tourvisit.entities.SigunguTable;
import org.g9project4.tourvisit.repositories.SigunguTableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


import static com.querydsl.apt.VisitorConfig.get;

@Service
@RequiredArgsConstructor
public class TourPlaceRepositoryCustomImpl implements TourPlaceRepositorycustom {

    private final JPAQueryFactory queryFactory;
    private final TourPlaceRepository tourPlaceRepository;
    private final SigunguTableRepository sigunguTableRepository;
    private final QTourPlace tourPlace = QTourPlace.tourPlace;


    @Transactional
    public List<TourPlace> updateSigunguCode2() {
        QTourPlace qTourPlace = QTourPlace.tourPlace;
        QSigunguTable qSigunguTable = QSigunguTable.sigunguTable;
        QSidoVisit qSidoVisit = QSidoVisit.sidoVisit;

        // Step 1: Retrieve all tour places
        List<TourPlace> tourPlaces = queryFactory.selectFrom(qTourPlace).fetch();

        // Step 2: Retrieve all sigungu tables and create a mapping
        List<SigunguTable> sigunguTables = queryFactory.selectFrom(qSigunguTable).fetch();
        Map<String, List<SigunguTable>> sigunguNameToCodeMap = sigunguTables.stream()
                .collect(Collectors.groupingBy(SigunguTable::getSigunguName));

        // Step 3: Retrieve all sido visits and create a mapping of areaCode to areaName
        List<SidoVisit> sidoVisits = queryFactory.selectFrom(qSidoVisit).fetch();
        Map<String, SidoVisit> areaCodeToSidoVisitMap = sidoVisits.stream()
                .collect(Collectors.toMap(SidoVisit::getAreaCode, Function.identity()));

        // Log the sigunguNameToCodeMap for debugging
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("SigunguName to Code Map: {}", sigunguNameToCodeMap);

        // Step 4: Update TourPlace with the appropriate SigunguCode2
        for (TourPlace tourPlace : tourPlaces) {
            String address = tourPlace.getAddress();
            String extractedSigunguName = extractSigunguNameFromAddress(address);

            if (extractedSigunguName != null) {
                // Case 1: Check if the extracted sigunguName matches any in the map
                List<SigunguTable> matchingSigunguTables = sigunguNameToCodeMap.get(extractedSigunguName);
                if (matchingSigunguTables != null && !matchingSigunguTables.isEmpty()) {
                    // Assume we take the first matching sigunguCode2
                    String sigunguCode2 = matchingSigunguTables.get(0).getSigunguCode2();
                    tourPlace.setSigunguCode2(sigunguCode2);
                } else {
                    // Case 2: Handle the scenario where sigunguName does not match
                    boolean foundMatch = false; // Flag to check if a match was found
                    for (SidoVisit sidoVisit : sidoVisits) {
                        String areaCode = sidoVisit.getAreaCode();
                        String areaName = sidoVisit.getAreaName().replaceAll("\\s+", ""); // Remove all whitespace

                        // Check if areaCode from SidoVisit matches sidoCode from SigunguTable
                        List<SigunguTable> matchingSigunguTablesBySidoCode = sigunguTables.stream()
                                .filter(table -> table.getSidoCode().equals(areaCode))
                                .collect(Collectors.toList());

                        for (SigunguTable sigunguTable : matchingSigunguTablesBySidoCode) {
                            String combinedName = (areaName + sigunguTable.getSigunguName()).replaceAll("\\s+", ""); // Combine and remove whitespace

                            // Log the combination for debugging
                            logger.info("Combining areaName: '{}' with sigunguName: '{}' to form: '{}'", areaName, sigunguTable.getSigunguName(), combinedName);

                            if (combinedName.equals(address.replaceAll("\\s+", ""))) { // Compare with address without whitespace
                                String sigunguCode2 = sigunguTable.getSigunguCode2();
                                tourPlace.setSigunguCode2(sigunguCode2);
                                foundMatch = true; // Mark that a match was found
                                break; // Exit the loop after updating
                            }
                        }
                        if (foundMatch) {
                            break; // Exit the outer loop if a match was found
                        }
                    }
                }
            }
        }

        // Step 5: Save updated TourPlace entities with SigunguCode2 updated
        List<TourPlace> updatedTourPlaces = tourPlaceRepository.saveAllAndFlush(tourPlaces);

        // Log the result for verification
        for (TourPlace tourPlace : updatedTourPlaces) {
            String address = tourPlace.getAddress();
            String extractedSigunguName = extractSigunguNameFromAddress(address);
            logger.info("TourPlace ContentID: {}, Extracted Sigungu Name: {}, SigunguCode2: {}",
                    tourPlace.getContentId(), extractedSigunguName, tourPlace.getSigunguCode2());
        }

        // Return the list of updated tour places
        return updatedTourPlaces;
    }

    public String extractSigunguNameFromAddress(String address) {
        if (address == null) {
            return null;
        }

        // Pattern to capture full region names including "도", "시", "군", "구" with preceding regional name
        Pattern pattern = Pattern.compile("([가-힣]+(?:특별시|광역시|도|시|군|구))?(?:\\s|$)([가-힣]+(?:시|군|구))(?:\\s|$)");
        Matcher matcher = pattern.matcher(address);

        // StringBuilder to concatenate all matched parts
        StringBuilder sigunguName = new StringBuilder();

        if (matcher.find()) {
            // Append the first match (optional region name like "경기도")
            if (matcher.group(1) != null) {
                sigunguName.append(matcher.group(1));
            }
            // Append the second match (main region name like "광주시")
            if (matcher.group(2) != null) {
                sigunguName.append(matcher.group(2));
            }
        }

        return sigunguName.toString();
    }



    @Override
    public List<TourPlace> getTourPlacesPoint(String contentTypeId) {

        // LocalDate 및 계절 조건 설정
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();


        // 계절에 따른 BooleanExpression 정의
        BooleanExpression isSummer = (currentMonth == 6 || currentMonth == 7 || currentMonth == 8) ?
                tourPlace.title.contains("해수욕장")
                        .or(tourPlace.title.contains("수상레저"))
                        .or(tourPlace.title.contains("휴양림"))
                        .or(tourPlace.title.contains("캠핑장"))
                : Expressions.asBoolean(false);

        BooleanExpression isWinter = (currentMonth == 12 || currentMonth == 1 || currentMonth == 2) ?
                tourPlace.title.contains("스키장")
                        .or(tourPlace.title.contains("해수욕장"))
                        .or(tourPlace.title.contains("수상레저"))
                : Expressions.asBoolean(false);

        BooleanExpression isSpringOrFall = (currentMonth >= 3 && currentMonth <= 5) || (currentMonth >= 9 && currentMonth <= 11) ?
                tourPlace.title.contains("스키장")
                        .or(tourPlace.title.contains("해수욕장"))
                        .or(tourPlace.title.contains("수상레저"))
                : Expressions.asBoolean(false);

        // QueryDSL 쿼리 작성
        List<TourPlace> tourPlaces = queryFactory
                .selectFrom(tourPlace)
                .where(
                        contentTypeId != null ? tourPlace.contentTypeId.eq(contentTypeId) : null,// Conditional filter // 필터링 예시
                        tourPlace.title.isNotNull(),
                        tourPlace.type1D1.isNotNull(),
                        tourPlace.type1M1.isNotNull(),
                        tourPlace.type1Y1.isNotNull(),
                        tourPlace.type2D1.isNotNull(),
                        tourPlace.type2M1.isNotNull(),
                        tourPlace.type2Y1.isNotNull(),
                        tourPlace.type3D1.isNotNull(),
                        tourPlace.type3M1.isNotNull(),
                        tourPlace.type3Y1.isNotNull()
                )
                .fetch(); // Fetch the list of TourPlace entities

        List<TourPlace> processedTourPlaces = tourPlaces.stream()
                .map(tourPlaceEntity -> {
                    // Calculate placePointValue
                    double totalPoints = tourPlaceEntity.getType1D1() + tourPlaceEntity.getType1M1() + tourPlaceEntity.getType1Y1()
                            + tourPlaceEntity.getType2D1() + tourPlaceEntity.getType2M1() + tourPlaceEntity.getType2Y1()
                            + tourPlaceEntity.getType3D1() + tourPlaceEntity.getType3M1() + tourPlaceEntity.getType3Y1();

                    Integer calculatedPlacePointValue = (int) Math.round(totalPoints / 396000.0);

                    // Apply seasonal adjustment
                    if (currentMonth == 6 || currentMonth == 7 || currentMonth == 8) {
                        if (tourPlaceEntity.getTitle().contains("해수욕장") ||
                                tourPlaceEntity.getTitle().contains("수상레저") ||
                                tourPlaceEntity.getTitle().contains("휴양림") ||
                                tourPlaceEntity.getTitle().contains("캠핑장")) {
                            calculatedPlacePointValue += 500;
                        } else if (tourPlaceEntity.getTitle().contains("스키장")) {
                            calculatedPlacePointValue -= 100;
                        }
                    } else if (currentMonth == 12 || currentMonth == 1 || currentMonth == 2) {
                        if (tourPlaceEntity.getTitle().contains("스키장")) {
                            calculatedPlacePointValue += 500;
                        } else if (tourPlaceEntity.getTitle().contains("해수욕장") ||
                                tourPlaceEntity.getTitle().contains("수상레저")) {
                            calculatedPlacePointValue -= 100;
                        }
                    } else if (currentMonth >= 3 && currentMonth <= 5 || currentMonth >= 9 && currentMonth <= 11) {
                        if (tourPlaceEntity.getTitle().contains("스키장") ||
                                tourPlaceEntity.getTitle().contains("해수욕장") ||
                                tourPlaceEntity.getTitle().contains("수상레저")) {
                            calculatedPlacePointValue -= 300;
                        }
                    }

                    return TourPlace.builder()
                            .contentId(tourPlaceEntity.getContentId())
                            .contentTypeId(tourPlaceEntity.getContentTypeId())
                            .title(tourPlaceEntity.getTitle())
                            .address(tourPlaceEntity.getAddress())
                            .placePointValue(calculatedPlacePointValue)
                            .build();
                })
                .collect(Collectors.toList());

        // Save results to repository using saveAllAndFlush
        return tourPlaceRepository.saveAllAndFlush(processedTourPlaces);
    }



}

