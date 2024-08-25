package org.g9project4.publicData.tourvisit.services;

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
import org.g9project4.member.entities.VisitRecord;
import org.g9project4.member.repositories.MemberRepository;

import org.g9project4.member.repositories.VisitRecordRepository;
import org.g9project4.publicData.tour.controllers.TourPlaceSearch;
import org.g9project4.publicData.tour.entities.QTourPlace;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TourplaceMRecordPointService {
    private final JPAQueryFactory queryFactory;
    private final MemberRepository memberRepository;
    private final TourPlaceRepository repository;
    private final EntityManager em;
    private final HttpServletRequest request;
    private final MemberUtil memberUtil;

    @Autowired
    private VisitRecordRepository visitRecordRepository;

    // 여행 추천 - 회원 방문기록 + 검색키워드 기반 + 베이스 점수 계산
    public ListData<TourPlace> getTopTourPlacesByRecord(TourPlaceSearch search, LocalDate currentDate) {
        QTourPlace qTourPlace = QTourPlace.tourPlace;
        QMember qMember = QMember.member;

        // 현재 로그인한 멤버의 정보를 가져옵니다.
        Member currentMember = memberUtil.getMember(); // 로그인된 멤버 정보



        // 모든 TourPlace 항목을 가져옵니다.
        List<TourPlace> tourPlaces = queryFactory.selectFrom(qTourPlace)
                .fetch();


        // 각 TourPlace에 대해 최종 점수를 계산하고 리스트를 생성합니다.
        List<TourPlace> topTourPlaces = tourPlaces.stream()
                .map(tourPlace -> {
                    // 각 Member별로 mRecordPoint를 계산
                    int mRecordPoint = currentMember.stream()
                            .mapToInt(member -> calculatePlacePointValue(tourPlace, currentMember, currentDate))
                            .sum();

                    // 최종 점수 계산
                    int finalPointValue = tourPlace.getPlacePointValue() + mRecordPoint;

                    // TourPlace의 finalPointValue를 업데이트하여 반환
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

    private int calculatePlacePointValue(TourPlace tourPlace, Member member, LocalDate currentDate) {
        int basePointValue = 0;

        // Fetch visit records and keywords for the member
        List<VisitRecord> visitRecords = fetchVisitRecords(member.getSeq(), currentDate);

        // Calculate points from visit records
        int visitPoints = visitRecords.stream()
                .filter(record -> record.getContentId().equals(tourPlace.getContentId()))
                .mapToInt(record -> Math.min(20 * record.getVisitCount(), 300))
                .sum();

        // Calculate points from search keywords
        int keywordPoints = visitRecords.stream()
                .filter(record -> record.getKeywords().stream()
                        .anyMatch(keyword -> tourPlace.getTitle().contains(keyword) || tourPlace.getAddress().contains(keyword)))
                .mapToInt(record -> {
                    int titlePoints = record.getKeywords().stream()
                            .anyMatch(keyword -> tourPlace.getTitle().contains(keyword)) ? 10 : 0;
                    int addressPoints = record.getKeywords().stream()
                            .anyMatch(keyword -> tourPlace.getAddress().contains(keyword)) ? 5 : 0;
                    return Math.min(titlePoints + addressPoints, 300);
                })
                .sum();

        basePointValue = visitPoints + keywordPoints;

        return Math.min(basePointValue, 300); // Apply daily limit
    }

    public List<VisitRecord> fetchVisitRecords(Long seq, LocalDate currentDate) {
        return visitRecordRepository.findByMemberAndVisitDate(seq, currentDate);
    }
}