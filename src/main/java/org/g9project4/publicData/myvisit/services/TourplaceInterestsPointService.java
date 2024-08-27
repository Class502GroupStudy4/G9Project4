package org.g9project4.publicData.myvisit.services;


import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.g9project4.global.ListData;
import org.g9project4.global.Pagination;
import org.g9project4.member.MemberUtil;
import org.g9project4.member.constants.Interest;
import org.g9project4.member.entities.Member;
import org.g9project4.member.repositories.InterestsRepository;
import org.g9project4.member.repositories.MemberRepository;
import org.g9project4.member.entities.Interests;
import org.g9project4.publicData.tour.controllers.TourPlaceSearch;
import org.g9project4.publicData.tour.entities.QTourPlace;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
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
    private final InterestsRepository interestsRepository;

    // 여행 추천 - 관심사 기반 + 베이스 점수 계산
    @Transactional(readOnly = true)
    public ListData<TourPlace> getTopTourPlacesByInterests(TourPlaceSearch search) {

        if (!memberUtil.isLogin()) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        Member member = memberUtil.getMember();
        List<Interest> interests = getInterestsForMember(member);

        if (interests.isEmpty()) {
            // Handle case where there are no interests
            return new ListData<>(Collections.emptyList(), new Pagination(1, 0, 0, search.getLimit(), request));
        }

        QTourPlace qTourPlace = QTourPlace.tourPlace;

        List<TourPlace> tourPlaces = queryFactory.selectFrom(qTourPlace).fetch();

        List<TourPlace> filteredTourPlaces = tourPlaces.stream()
                .filter(tourPlace -> filterByInterests(tourPlace, interests))
                .collect(Collectors.toList());

        List<TourPlace> topTourPlaces = filteredTourPlaces.stream()
                .map(tourPlace -> {
                    int interestPoints = calculateInterestPoints(tourPlace, interests);
                    int finalPointValue = tourPlace.getPlacePointValue() + interestPoints;
                    tourPlace.setPlacePointValue(finalPointValue);
                    return tourPlace;
                })
                .sorted((tp1, tp2) -> Integer.compare(tp2.getPlacePointValue(), tp1.getPlacePointValue()))
                .limit(20)
                .collect(Collectors.toList());

        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit < 1 ? 10 : limit;
        int offset = (page - 1) * limit;

        Pagination pagination = new Pagination(page, (int) repository.count(), 0, limit, request);
        List<TourPlace> items = queryFactory.selectFrom(qTourPlace)
                .orderBy(qTourPlace.placePointValue.desc())
                .offset(offset)
                .limit(limit)
                .fetch();

        return new ListData<>(items, pagination);
    }

    @Transactional(readOnly = true)
  public List<Interest> getInterestsForMember(Member member) {
        // 회원의 관심사 데이터 조회
        if (member == null) {
            throw new IllegalArgumentException("The given id must not be null");
        }

        Member managedMember = memberRepository.findById(member.getSeq())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        List<Interests> interestsEntities = interestsRepository.findByMember(managedMember);
        return interestsEntities.stream()
                .map(Interests::getInterest)
                .collect(Collectors.toList());

    }


    private boolean filterByInterests(TourPlace tourPlace, List<Interest> interests) {
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

    private int calculateInterestPoints(TourPlace tourPlace, List<Interest> interests) {
        int interestCount = 0;
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