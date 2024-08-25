package org.g9project4.publicData.tour.repositories;

import org.g9project4.publicData.tour.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CategoryRepository extends JpaRepository<Category,Long>, QuerydslPredicateExecutor<Category> {
    @Query("SELECT DISTINCT c.category1 FROM Category c")
    List<String> findDistinctCategory1();
    @Query("SELECT DISTINCT c.category2 FROM Category c WHERE c.category1 = :category1")

    List<String> findDistinctCategory2ByCategory1(@Param("category1")String category1);
    @Query("SELECT DISTINCT c.category3 FROM Category c WHERE c.category2 = :category2")
    List<String> findDistinctCategory3ByCategory2(@Param("category2") String category2);
}
