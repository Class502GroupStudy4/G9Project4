package org.g9project4.tourvisit.repositories;

import org.g9project4.tourvisit.entities.SidoVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface  SidoVisitRepository extends JpaRepository<SidoVisit, String>, QuerydslPredicateExecutor<SidoVisit>{
}
