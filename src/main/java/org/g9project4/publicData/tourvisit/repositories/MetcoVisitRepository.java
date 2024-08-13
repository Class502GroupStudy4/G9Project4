package org.g9project4.publicData.tourvisit.repositories;

import org.g9project4.publicData.tourvisit.entities.LocgoVisitId;
import org.g9project4.publicData.tourvisit.entities.MetcoVisit;
import org.g9project4.publicData.tourvisit.entities.MetcoVisitId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface MetcoVisitRepository extends JpaRepository<MetcoVisit, MetcoVisitId>, QuerydslPredicateExecutor<MetcoVisit> {
}
