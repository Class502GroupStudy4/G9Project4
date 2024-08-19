package org.g9project4.mypage.repositories;

import org.g9project4.mypage.entities.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findByMemberSeqOrderBySearchTimeDesc(Long memberSeq);
}
