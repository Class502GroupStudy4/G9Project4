package org.g9project4.tourvisit.services;


import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.g9project4.member.entities.Member;
import org.g9project4.member.entities.QMember;
import org.g9project4.member.repositories.MemberRepository;
import org.g9project4.publicData.tour.entities.QTourPlace;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TourplacePointMemberService {

    private final JPAQueryFactory queryFactory;
    private final MemberRepository memberRepository;

    // 여행 추천 - 회원 정보 기반 + 베이스 점수 계산
    public List<TourPlace> getTopTourPlacesByMemberSeq(LocalDate currentDate) {
        QTourPlace qTourPlace = QTourPlace.tourPlace;
        QMember qMember = QMember.member;

        // 모든 TourPlace 항목을 가져옵니다.
        List<TourPlace> tourPlaces = queryFactory.selectFrom(qTourPlace)
                .fetch();

        // 모든 Member 항목을 가져옵니다.
        List<Member> allMembers = memberRepository.findAll();

        // 각 TourPlace에 대해 placePointValue를 계산하고 리스트를 생성합니다.
        List<TourPlace> topTourPlaces = tourPlaces.stream()
                .map(tourPlace -> {
                    // 각 Member별로 placePointValue 계산
                    int totalPoints = allMembers.stream()
                            .mapToInt(member -> calculatePlacePointValue(tourPlace, member, currentDate))
                            .sum();

                    // TourPlace의 placePointValue를 업데이트하여 반환
                    tourPlace.setPlacePointValue(totalPoints);
                    return tourPlace;
                })
                .sorted((tp1, tp2) -> Integer.compare(tp2.getPlacePointValue(), tp1.getPlacePointValue())) // 내림차순 정렬
                .limit(20) // 상위 20개 항목만 반환
                .collect(Collectors.toList());

        return topTourPlaces;
    }

    private int calculatePlacePointValue(TourPlace tourPlace, Member member, LocalDate currentDate) {
        int age = calculateAge(member.getBirth(), currentDate);
        String currentSeason = getCurrentSeason(currentDate);
        int basePointValue = tourPlace.getPlacePointValue(); // 기본 점수
        int additionalPoints = 0;

        // 연령대, 외국인 여부, 현재 계절 및 title 기준으로 추가 점수 계산
        if (age >= 10 && age < 30) {
            if (currentSeason.equals("summer") && tourPlace.getTitle().matches(".*(해수욕장|수상레저).*")) {
                additionalPoints += 200;
            } else if (currentSeason.equals("winter") && tourPlace.getTitle().matches(".*(스키장).*")) {
                additionalPoints += 200;
            } else if (currentSeason.equals("spring") || currentSeason.equals("fall")) {
                if (tourPlace.getTitle().matches(".*(산|공원).*")) {
                    additionalPoints += 200;
                }
            }
        } else if (age >= 30 && age < 50) {
            if (currentSeason.equals("summer") && tourPlace.getTitle().matches(".*(박물관|글램핑|공원).*")) {
                additionalPoints += 200;
            } else if (currentSeason.equals("winter") && tourPlace.getTitle().matches(".*(스키장|캠핑장|공원).*")) {
                additionalPoints += 200;
            } else if (currentSeason.equals("spring") || currentSeason.equals("fall")) {
                if (tourPlace.getTitle().matches(".*(산|공원|박물관).*")) {
                    additionalPoints += 200;
                }
            }
        } else if (age >= 50) {
            if (currentSeason.equals("summer") && tourPlace.getTitle().matches(".*(휴양림|캠핑장).*")) {
                additionalPoints += 200;
            } else if (currentSeason.equals("winter") && tourPlace.getTitle().matches(".*(휴양림|스파).*")) {
                additionalPoints += 200;
            } else if (currentSeason.equals("spring") || currentSeason.equals("fall")) {
                if (tourPlace.getTitle().matches(".*(산|공원|스파).*")) {
                    additionalPoints += 200;
                }
            }
        }

        return basePointValue + additionalPoints; // basePointValue와 추가 점수를 합산하여 반환
    }

    private int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        return Period.between(birthDate, currentDate).getYears();
    }

    private String getCurrentSeason(LocalDate currentDate) {
        int month = currentDate.getMonthValue();
        if (month >= 3 && month <= 5) {
            return "spring";
        } else if (month >= 6 && month <= 8) {
            return "summer";
        } else if (month >= 9 && month <= 11) {
            return "fall";
        } else {
            return "winter";
        }
    }
}