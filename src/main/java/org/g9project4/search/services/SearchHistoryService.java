package org.g9project4.search.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.g9project4.global.ListData;
import org.g9project4.global.Pagination;
import org.g9project4.member.MemberUtil;
import org.g9project4.member.entities.Member;
import org.g9project4.member.repositories.MemberRepository;
import org.g9project4.search.constatnts.SearchType;
import org.g9project4.search.entities.SearchHistory;
import org.g9project4.search.repositories.SearchHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {
    private final SearchHistoryRepository repository;
    private final MemberRepository memberRepository;
    private final MemberUtil memberUtil;
    private final HttpServletRequest request;

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

    //public Pagination(int page, int total, int ranges, int limit, HttpServletRequest request)
    public ListData<SearchHistory> getSearchHistoryForMember(Member member, Pageable pageable) {
        int page = pageable.getPageNumber();
        int limit = pageable.getPageSize()
        Pageable pageable = PageRequest.of(page - 1, 5); // 페이지와 페이지 크기를 설정
        Page<SearchHistory> searchHistories = repository.findByMember(member, pageable);
        Pagination pagination = new Pagination(page, (int) searchHistories.getTotalElements(), 5, size, request);

        List<SearchHistory> items = searchHistories.getContent();

        return new ListData<>(items, pagination);
    }
}
