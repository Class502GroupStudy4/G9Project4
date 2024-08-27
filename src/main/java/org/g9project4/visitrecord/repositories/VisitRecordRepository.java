//package org.g9project4.visitrecord.repositories;
//
//import org.g9project4.member.entities.Member;
//import org.g9project4.search.entities.SearchHistory;
//import org.g9project4.visitrecord.entities.VisitRecords;
//import org.g9project4.visitrecord.entities.VisitRecordsId;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.querydsl.QuerydslPredicateExecutor;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.g9project4.publicData.tour.entities.QTourPlace.tourPlace;
//
//public interface VisitRecordRepository extends JpaRepository<VisitRecords, VisitRecordsId> {
//
//    List<VisitRecords> findByMember(Member member);
//
//}