//package org.g9project4.member.repositories;
//
//import org.g9project4.member.entities.VisitRecord;
//import org.g9project4.member.entities.VisitRecordsId;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.querydsl.QuerydslPredicateExecutor;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.g9project4.publicData.tour.entities.QTourPlace.tourPlace;
//
//public interface VisitRecordRepository extends JpaRepository<VisitRecord, VisitRecordsId>, QuerydslPredicateExecutor<VisitRecord> {
//
//    // 메서드는 특정 회원(memberId)과 방문 날짜(visitDate)를 기준으로 VisitRecord 엔티티를 조회하는 데 사용됩니다.
//   List<VisitRecord> findByMemberAndVisitDate(Long seq, LocalDate visitDate);
//
//    // 특정 contentId로 VisitRecord를 조회
//    List<VisitRecord> findByTourPlace_ContentId(Long contentId);
//
//    // 특정 member와 tourPlace의 contentId로 VisitRecord를 조회
//    List<VisitRecord> findByMemberAndTourPlace_ContentId(Long seq, Long contentId);
//
//}