package org.g9project4.publicData.tourvisit.repositories;

import org.g9project4.publicData.tourvisit.entities.LocgoVisit;
import org.g9project4.publicData.tourvisit.entities.LocgoVisitId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;


public interface LocgoVisitRepository extends JpaRepository<LocgoVisit, LocgoVisitId>, QuerydslPredicateExecutor<LocgoVisit> {
}
