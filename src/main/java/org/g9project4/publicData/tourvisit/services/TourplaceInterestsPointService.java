package org.g9project4.publicData.tourvisit.services;


import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.g9project4.global.ListData;
import org.g9project4.global.Pagination;
import org.g9project4.member.MemberUtil;
import org.g9project4.member.constants.Interest;
import org.g9project4.member.entities.Member;
import org.g9project4.member.entities.QMember;
import org.g9project4.member.repositories.MemberRepository;

import org.g9project4.publicData.tour.controllers.TourPlaceSearch;
import org.g9project4.publicData.tour.entities.QTourPlace;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class TourplaceInterestsPointService {

    private final JPAQueryFactory queryFactory;
    private final MemberRepository memberRepository;
    private final TourPlaceRepository repository;
    private final HttpServletRequest request;
    private final EntityManager em;
    private final MemberUtil memberUtil;

    // 여행 추천 - 관심사 기반 + 베이스 점수 계산
    public ListData<TourPlace> getTopTourPlacesByInterests(Long seq, TourPlaceSearch search) {

        QTourPlace qTourPlace = QTourPlace.tourPlace;
        QMember qMember = QMember.member;
        // 현재 로그인한 멤버의 정보를 가져옵니다.
        Member currentMember = memberUtil.getMember(); // 로그인된 멤버 정보
        int age = calculateAge(currentMember.getInterests());


        if (!memberUtil.isLogin()) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }



        // 관심사 데이터를 가져옵니다.
        List<String> interests = getInterestsAsList(member);

        // 모든 TourPlace 항목을 가져옵니다.
        List<TourPlace> tourPlaces = queryFactory.selectFrom(qTourPlace)
                .fetch();

        // 관심사 기반으로 필터링된 TourPlace 목록을 생성합니다.
        List<TourPlace> filteredTourPlaces = tourPlaces.stream()
                .filter(tourPlace -> filterByInterests(tourPlace, interests))
                .collect(Collectors.toList());

        // 각 TourPlace에 대해 최종 점수를 계산하고 리스트를 생성합니다.
        List<TourPlace> topTourPlaces = filteredTourPlaces.stream()
                .map(tourPlace -> {
                    // 관심사 기반 점수 계산
                    int interestPoints = calculateInterestPoints(tourPlace, interests);

                    // 최종 점수 계산
                    int finalPointValue = tourPlace.getPlacePointValue() + interestPoints;

                    // TourPlace의 finalPointValue를 업데이트하여 반환 (레포지토리에 저장하지 않음)
                    tourPlace.setPlacePointValue(finalPointValue);

                    return tourPlace;
                })
                .sorted((tp1, tp2) -> Integer.compare(tp2.getPlacePointValue(), tp1.getPlacePointValue())) // 내림차순 정렬
                .limit(20) // 최대 20개의 항목만 가져오기
                .collect(Collectors.toList());


        // 전체 항목 수 계산 (최대 20개로 제한)
        int totalItems = topTourPlaces.size();

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

    private List<String> getInterestsAsList(Member member) {
        List<String> interestsList = new ArrayList<>();
        Interest interest = member.getInterests();

        if (interest != null) {
            interestsList.add(interest.name());
        }

        return interestsList;
    }


    private boolean filterByInterests(TourPlace tourPlace, List<String> interests) {
        // 관심사에 따라 필터링 수행

        if (interests.contains("MATJIB") && tourPlace.getContentTypeId() == 12) {
            return true;
        }


        if (interests.contains("HOCANCE") && tourPlace.getTitle().contains("호텔")) {
            return true;
        }
        if (interests.contains("MUSEUM") &&
                (tourPlace.getTitle().contains("문학관") ||
                        tourPlace.getTitle().contains("박물관") ||
                        tourPlace.getTitle().contains("테마관") ||
                        tourPlace.getTitle().contains("문화관") ||
                        tourPlace.getTitle().contains("과학관"))) {
            return true;
        }
        if (interests.contains("CAMPING") &&
                (tourPlace.getTitle().contains(" 캠핑장") ||
                        tourPlace.getTitle().contains("야영장") ||
                        tourPlace.getTitle().contains("글램핑") ||
                        tourPlace.getTitle().contains("카라반"))) {
            return true;
        }
        if (interests.contains("HIKING") && tourPlace.getTitle().contains("산")) {

            return true;
        }
        if (interests.contains("NATURE") &&
                (tourPlace.getTitle().contains("공원") ||
                        tourPlace.getTitle().contains("산") ||
                        tourPlace.getTitle().contains("휴양림") ||
                        tourPlace.getTitle().contains("케이블카") ||
                        tourPlace.getTitle().contains("계곡") ||
                        tourPlace.getTitle().contains("펜션"))) {
            return true;

        }
        if (interests.contains("ART") && tourPlace.getTitle().contains("미술관")) {

            return true;
        }

        if (interests.contains("SEA") &&
                (tourPlace.getTitle().contains("해수욕장") ||
                        tourPlace.getTitle().contains("강") ||
                        tourPlace.getTitle().contains("계곡") ||
                        tourPlace.getTitle().contains("해변") ||
                        tourPlace.getTitle().contains("선착장"))) {
            return true;

        }
        if (interests.contains("WITHCHILD") &&
                (tourPlace.getTitle().contains("물놀이장") ||
                        tourPlace.getTitle().contains("목장") ||
                        tourPlace.getTitle().contains("농장") ||
                        tourPlace.getTitle().contains("체험마을") ||
                        tourPlace.getTitle().contains("테마관") ||
                        tourPlace.getTitle().contains("해변") ||
                        tourPlace.getTitle().contains("캠프") ||
                        tourPlace.getTitle().contains("과학관") ||
                        tourPlace.getTitle().contains("마을") ||
                        tourPlace.getTitle().contains("체험관") ||
                        tourPlace.getTitle().contains("축제") ||
                        tourPlace.getTitle().contains("수영장"))) {
            return true;

        }

        if (interests.contains("WITHFAMILY") &&
                (tourPlace.getTitle().contains("리조트") ||
                        tourPlace.getTitle().contains("펜션") ||
                        tourPlace.getTitle().contains("농장") ||
                        tourPlace.getTitle().contains("휴양림"))) {
            return true;

        }
        if (interests.contains("WITHLOVER") &&
                (tourPlace.getTitle().contains("카페") ||
                        tourPlace.getTitle().contains("해수욕장") ||
                        tourPlace.getTitle().contains("워터파크") ||
                        tourPlace.getTitle().contains("수상레저") ||
                        tourPlace.getTitle().contains("캠핑장") ||
                        tourPlace.getTitle().contains("글램핑"))) {
            return true;

        }
        if (interests.contains("FISHING") && tourPlace.getTitle().contains("낚시")) {

            return true;
        }

        return false;
    }

    private int calculateInterestPoints(TourPlace tourPlace, List<String> interests) {
        boolean isMatjibIncluded = false;
        boolean isHocanceIncluded = false;
        boolean isMuseumIncluded = false;
        boolean isCampingIncluded = false;
        boolean isHikingIncluded = false;
        boolean isNatureIncluded = false;
        boolean isArtIncluded = false;
        boolean isSeaIncluded = false;
        boolean isWithChildIncluded = false;
        boolean isWithfamilyIncluded = false;
        boolean isWithloverIncluded = false;
        boolean isFishingIncluded = false;


        // 관심사와 TourPlace의 제목을 매칭하여 점수 계산

        if (interests.contains("MATJIB") && tourPlace.getContentTypeId() == 12) {
            isMatjibIncluded = true;
        }

        if (interests.contains("HOCANCE") && tourPlace.getTitle().contains("호텔")) {
            isHocanceIncluded = true;
        }
        if (interests.contains("MUSEUM") &&
                (tourPlace.getTitle().contains("문학관") ||
                        tourPlace.getTitle().contains("박물관") ||
                        tourPlace.getTitle().contains("테마관") ||
                        tourPlace.getTitle().contains("문화관") ||
                        tourPlace.getTitle().contains("과학관"))) {
            isMuseumIncluded = true;
        }
        if (interests.contains("CAMPING") &&
                (tourPlace.getTitle().contains(" 캠핑장") ||
                        tourPlace.getTitle().contains("야영장") ||
                        tourPlace.getTitle().contains("글램핑") ||
                        tourPlace.getTitle().contains("카라반"))) {
            isCampingIncluded = true;
        }
        if (interests.contains("HIKING") && tourPlace.getTitle().contains("산")) {

            isHikingIncluded = true;
        }
        if (interests.contains("NATURE") &&
                (tourPlace.getTitle().contains("공원") ||
                        tourPlace.getTitle().contains("산") ||
                        tourPlace.getTitle().contains("휴양림") ||
                        tourPlace.getTitle().contains("케이블카") ||
                        tourPlace.getTitle().contains("계곡") ||
                        tourPlace.getTitle().contains("펜션"))) {
            isNatureIncluded = true;

        }
        if (interests.contains("ART") && tourPlace.getTitle().contains("미술관")) {

            isArtIncluded = true;
        }

        if (interests.contains("SEA") &&
                (tourPlace.getTitle().contains("해수욕장") ||
                        tourPlace.getTitle().contains("강") ||
                        tourPlace.getTitle().contains("계곡") ||
                        tourPlace.getTitle().contains("해변") ||
                        tourPlace.getTitle().contains("선착장"))) {
            isSeaIncluded = true;

        }
        if (interests.contains("WITHCHILD") &&
                (tourPlace.getTitle().contains("물놀이장") ||
                        tourPlace.getTitle().contains("목장") ||
                        tourPlace.getTitle().contains("농장") ||
                        tourPlace.getTitle().contains("체험마을") ||
                        tourPlace.getTitle().contains("테마관") ||
                        tourPlace.getTitle().contains("해변") ||
                        tourPlace.getTitle().contains("캠프") ||
                        tourPlace.getTitle().contains("과학관") ||
                        tourPlace.getTitle().contains("마을") ||
                        tourPlace.getTitle().contains("체험관") ||
                        tourPlace.getTitle().contains("축제") ||
                        tourPlace.getTitle().contains("수영장"))) {
            isWithChildIncluded = true;

        }

        if (interests.contains("WITHFAMILY") &&
                (tourPlace.getTitle().contains("리조트") ||
                        tourPlace.getTitle().contains("펜션") ||
                        tourPlace.getTitle().contains("농장") ||
                        tourPlace.getTitle().contains("휴양림"))) {
            isWithfamilyIncluded = true;

        }
        if (interests.contains("WITHLOVER") &&
                (tourPlace.getTitle().contains("카페") ||
                        tourPlace.getTitle().contains("해수욕장") ||
                        tourPlace.getTitle().contains("워터파크") ||
                        tourPlace.getTitle().contains("수상레저") ||
                        tourPlace.getTitle().contains("캠핑장") ||
                        tourPlace.getTitle().contains("글램핑"))) {
            isWithloverIncluded = true;

        }
        if (interests.contains("FISHING") && tourPlace.getTitle().contains("낚시")) {
            isFishingIncluded = true;
        }


        int interestCount = 0;
        if (isMatjibIncluded) {
            interestCount++;
        }
        if (isHocanceIncluded) {
            interestCount++;
        }
        if (isMuseumIncluded) {
            interestCount++;
        }
        if (isCampingIncluded) {
            interestCount++;
        }
        if (isHikingIncluded) {
            interestCount++;
        }
        if (isNatureIncluded) {
            interestCount++;
        }
        if (isArtIncluded) {
            interestCount++;
        }
        if (isSeaIncluded) {
            interestCount++;
        }
        if (isWithChildIncluded) {
            interestCount++;
        }
        if (isWithfamilyIncluded) {
            interestCount++;
        }
        if (isWithloverIncluded) {
            interestCount++;
        }
        if (isWithfamilyIncluded) {
            interestCount++;
        }
        if (isFishingIncluded) {
            interestCount++;
        }


        // 관심사에 따른 추가 점수 계산
        if (interestCount == 1) {
            return 100;
        } else if (interestCount == 2) {
            return 200;
        } else if (interestCount >= 3) {
            return 300;
        }

        return 0;
    }
}