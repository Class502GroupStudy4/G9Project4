package org.g9project4.mypage.services;

import org.g9project4.mypage.entities.SearchHistory;
import org.g9project4.mypage.repositories.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchHistoryService {

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    public List<SearchHistory> getSearchHistory(Long memberSeq) {
        return searchHistoryRepository.findByMemberSeqOrderBySearchTimeDesc(memberSeq);
    }
}
