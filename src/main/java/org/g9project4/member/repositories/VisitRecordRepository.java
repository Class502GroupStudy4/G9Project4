package org.g9project4.member.repositories;

import org.g9project4.member.entities.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDate;
import java.util.List;

public interface VisitRecordRepository extends JpaRepository<VisitRecord, Long>, QuerydslPredicateExecutor<VisitRecord> {

    // 메서드는 특정 회원(memberId)과 방문 날짜(visitDate)를 기준으로 VisitRecord 엔티티를 조회하는 데 사용됩니다.
   List<VisitRecord> findByMemberAndVisitDate(Long seq, LocalDate visitDate);
}