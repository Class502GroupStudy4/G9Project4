package org.g9project4.search.services;

import lombok.RequiredArgsConstructor;
import org.g9project4.member.MemberUtil;
import org.g9project4.member.entities.Member;
import org.g9project4.member.repositories.MemberRepository;
import org.g9project4.search.constatnts.SearchType;
import org.g9project4.search.entities.SearchHistory;
import org.g9project4.search.repositories.SearchHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {
    private final SearchHistoryRepository repository;
    private final MemberRepository memberRepository;
    private final MemberUtil memberUtil;

    public void save(String keyword, SearchType type) {
        if (!memberUtil.isLogin() || keyword == null || !StringUtils.hasText(keyword.trim())) {
            return;
        }
        try {
            SearchHistory history = SearchHistory.builder()
                    .keyword(keyword)
                    .member(memberRepository.getReferenceById(memberUtil.getMember().getSeq()))
                    .searchType(type)
                    .build();
            System.out.println(history);
            repository.save(history);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveBoard(String keyword) {
        save(keyword, SearchType.BOARD);
    }

    public void saveTour(String keyword) {
        save(keyword, SearchType.TOUR);
    }

    public List<SearchHistory> getSearchHistoryForMember(Member member) {
        return repository.findByMember(member);
    }
}
