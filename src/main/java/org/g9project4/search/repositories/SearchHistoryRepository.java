package org.g9project4.search.repositories;

import org.g9project4.member.entities.Member;
import org.g9project4.search.entities.SearchHistory;
import org.g9project4.search.entities.SearchHistoryId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, SearchHistoryId> {
    Page<SearchHistory> findByMember(Member member, Pageable pageable);
}
