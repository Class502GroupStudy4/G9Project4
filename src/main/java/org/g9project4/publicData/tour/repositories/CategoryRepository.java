package org.g9project4.publicData.tour.repositories;

import org.g9project4.publicData.tour.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CategoryRepository extends JpaRepository<Category,Long>, QuerydslPredicateExecutor<Category> {
}
