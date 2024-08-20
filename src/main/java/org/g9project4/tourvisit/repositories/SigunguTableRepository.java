package org.g9project4.tourvisit.repositories;

import org.g9project4.tourvisit.entities.SigunguTable;
import org.g9project4.tourvisit.entities.SigunguVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SigunguTableRepository  extends JpaRepository<SigunguTable, String>, QuerydslPredicateExecutor<SigunguTable> {
}
