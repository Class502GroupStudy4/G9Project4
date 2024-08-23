package org.g9project4.publicData.tourvisit.services;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.g9project4.global.ListData;
import org.g9project4.global.Pagination;
import org.g9project4.member.entities.Member;
import org.g9project4.member.entities.QMember;
import org.g9project4.member.entities.VisitRecord;
import org.g9project4.member.repositories.MemberRepository;
import org.g9project4.member.repositories.VisitRecordRepository;
import org.g9project4.publicData.tour.entities.QTourPlace;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
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
    private final TourPlaceRepository tourPlaceRepository;

    // 여행 추천 - 회원 방문기록 + 검색키워드 기반 + 베이스 점수 계산
    public ListData<TourPlace> getTopTourPlacesByMemberSeq(LocalDate currentDate, int pageNumber, int pageSize) {
        QTourPlace qTourPlace = QTourPlace.tourPlace;

        // 모든 TourPlace 항목을 가져옵니다.
        List<TourPlace> tourPlaces = queryFactory.selectFrom(qTourPlace)
                .fetch();

        // 모든 Member 항목을 가져옵니다.
        List<Member> allMembers = memberRepository.findAll();

        // 각 TourPlace에 대해 최종 점수를 계산하고 리스트를 생성합니다.
        List<TourPlace> topTourPlaces = tourPlaces.stream()
                .map(tourPlace -> {
                    // 각 Member별로 mRecordPoint를 계산
                    int mRecordPoint = allMembers.stream()
                            .mapToInt(member -> calculatePlacePointValue(tourPlace, member, currentDate))
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

        // 페이지 번호와 크기에 따른 페이징 적용
        List<TourPlace> paginatedTopTourPlaces = topTourPlaces.stream()
                .skip((pageNumber - 1) * pageSize) // 페이지 계산
                .limit(pageSize) // 페이지 크기 적용 (한 페이지당 10개)
                .collect(Collectors.toList());

        // Pagination 객체 생성
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        Pagination pagination = new Pagination(pageNumber, totalItems, totalPages, pageSize, null);

        // ListData로 변환하여 반환
        return new ListData<>(paginatedTopTourPlaces, pagination);
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

    private List<VisitRecord> fetchVisitRecords(Long memberId, LocalDate currentDate) {
        // 방문 기록 가져오는 로직 (현재는 빈 리스트 반환)
        return Collections.emptyList();
    }
}