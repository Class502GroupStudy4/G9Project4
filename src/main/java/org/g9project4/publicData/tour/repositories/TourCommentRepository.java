package org.g9project4.publicData.tour.repositories;

import org.g9project4.publicData.tour.entities.TourCommentData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface TourCommentRepository extends JpaRepository<TourCommentData, Long>, QuerydslPredicateExecutor<TourCommentData> {
}
