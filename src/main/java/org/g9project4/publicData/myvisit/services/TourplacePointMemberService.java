package org.g9project4.publicData.myvisit.services;


import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.g9project4.global.ListData;
import org.g9project4.global.Pagination;
import org.g9project4.member.MemberUtil;
import org.g9project4.member.entities.Member;
import org.g9project4.member.entities.QMember;
import org.g9project4.member.repositories.MemberRepository;
import org.g9project4.publicData.tour.controllers.TourPlaceSearch;
import org.g9project4.publicData.tour.entities.QTourPlace;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TourplacePointMemberService {

    private final JPAQueryFactory queryFactory;
    private final MemberRepository memberRepository;
    private final TourPlaceRepository repository;
    private final EntityManager em;
    private final HttpServletRequest request;
private final MemberUtil memberUtil;

    public ListData<TourPlace> getTopTourPlacesByMember(TourPlaceSearch search, LocalDate currentDate) {
        QTourPlace qTourPlace = QTourPlace.tourPlace;
        QMember qMember = QMember.member;

        // 현재 로그인한 멤버의 정보를 가져옵니다.
        Member currentMember = memberUtil.getMember(); // 로그인된 멤버 정보
        int age = calculateAge(currentMember.getBirth(), currentDate);


        // 모든 TourPlace 항목을 가져옵니다.
        List<TourPlace> tourPlaces = queryFactory.selectFrom(qTourPlace)
                .fetch();

        // 현재 계절 계산
        String currentSeason = getCurrentSeason(currentDate);

        // 각 TourPlace에 대해 최종 점수를 계산하고 리스트를 생성합니다.
        List<TourPlace> topTourPlaces = tourPlaces.stream()
                .map(tourPlace -> {
                    // 각 Member별로 mRecordPoint를 계산
                    int mRecordPoint = calculateMRecordPoint(tourPlace, currentMember, currentDate, currentSeason);

                    // 최종 점수 계산 (placePointValue + mRecordPoint)
                    int finalPointValue = tourPlace.getPlacePointValue() + mRecordPoint;

                    // 최종 점수를 TourPlace 객체에 설정 (저장하지 않음, 일시적으로 사용)
                    tourPlace.setPlacePointValue(finalPointValue);
                    return tourPlace;
                })
                .sorted((tp1, tp2) -> Integer.compare(tp2.getPlacePointValue(), tp1.getPlacePointValue())) // 내림차순 정렬
                .limit(20) // 최대 20개의 항목만 가져오기
                .collect(Collectors.toList());

        // 전체 항목 수 계산
        int totalItems = Math.min(topTourPlaces.size(), 20); // 최대 20개 항목으로 제한

        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit < 1 ? 10 : limit;
        int offset = (page - 1) * limit;

        Pagination pagination = new Pagination(page, (int) repository.count(), 0, limit, request);
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QTourPlace tourPlace = QTourPlace.tourPlace;
        List<TourPlace> items = queryFactory.selectFrom(tourPlace)
                .orderBy(tourPlace.placePointValue.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
        return new ListData<>(items, pagination);
    }

    private int calculateMRecordPoint(TourPlace tourPlace, Member member, LocalDate currentDate, String currentSeason) {
        int age = calculateAge(member.getBirth(), currentDate);
        int mRecordPoint = 0;

        // 연령대 및 현재 계절 기준으로 추가 점수 계산
        if (age >= 10 && age < 30) {
            if (currentSeason.equals("summer") && tourPlace.getTitle().matches(".*(해수욕장|수상레저).*")) {
                mRecordPoint += 200;
            } else if (currentSeason.equals("winter") && tourPlace.getTitle().matches(".*(스키장).*")) {
                mRecordPoint += 200;
            } else if (currentSeason.equals("spring") || currentSeason.equals("fall")) {
                if (tourPlace.getTitle().matches(".*(산|공원).*")) {
                    mRecordPoint += 200;
                }
            }
        } else if (age >= 30 && age < 50) {
            if (currentSeason.equals("summer") && tourPlace.getTitle().matches(".*(박물관|글램핑|공원).*")) {
                mRecordPoint += 200;
            } else if (currentSeason.equals("winter") && tourPlace.getTitle().matches(".*(스키장|캠핑장|공원).*")) {
                mRecordPoint += 200;
            } else if (currentSeason.equals("spring") || currentSeason.equals("fall")) {
                if (tourPlace.getTitle().matches(".*(산|공원|박물관).*")) {
                    mRecordPoint += 200;
                }
            }
        } else if (age >= 50) {
            if (currentSeason.equals("summer") && tourPlace.getTitle().matches(".*(휴양림|캠핑장).*")) {
                mRecordPoint += 200;
            } else if (currentSeason.equals("winter") && tourPlace.getTitle().matches(".*(휴양림|스파).*")) {
                mRecordPoint += 200;
            } else if (currentSeason.equals("spring") || currentSeason.equals("fall")) {
                if (tourPlace.getTitle().matches(".*(산|공원|스파).*")) {
                    mRecordPoint += 200;
                }
            }
        }

        return mRecordPoint;
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