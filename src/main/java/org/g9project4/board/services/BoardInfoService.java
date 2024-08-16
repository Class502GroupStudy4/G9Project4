package org.g9project4.board.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.g9project4.board.controllers.BoardDataSearch;
import org.g9project4.board.entities.BoardData;
import org.g9project4.board.entities.QBoardData;
import org.g9project4.board.repositories.BoardDataRepository;
import org.g9project4.global.ListData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardInfoService {
    private final JPAQueryFactory jpaQueryFactory;
    private final BoardDataRepository repository;
    public ListData<BoardData> getList(BoardDataSearch search){
        int page =Math.max(search.getPage(),1);
        int limit = search.getLimit();

        String sopt = search.getSort();
        String skey = search.getSkey();

        String bid = search.getBid();
        List<String> bids = search.getBids();

        /* 검색 처리 */
        QBoardData boardData = QBoardData.boardData;
        BooleanBuilder andBuilder = new BooleanBuilder();

        if(bid != null && StringUtils.hasText(bid.trim())){
            //게시판별 조회  trim 일때 null이면 오류발생
            andBuilder.and(boardData.board.bid.eq(bid));
        }else if(bids != null && !bids.isEmpty()){
            andBuilder.and(boardData.board.bid.in(bids));
        }

        /*
        조건 검색 처리
        sop -All : 통합검색(제목 + 내용 + 글작성자(작성자, 회원명))
        subject : 제목검색
        content : 내용검색
        subject_content: 제목 + 내용 검색
        name : 이름( 작성자, 회원명)
         */
        sopt = sopt != null && StringUtils.hasText(sopt.trim()) ? sopt.trim() : "ALL";
        if(skey !=null && StringUtils.hasText(skey.trim())){}
        /* 검색 처리 e */
        return null;
    }

    //정해진 게시판에서 여려개 검색
    public ListData<BoardData> getList(String bid, BoardDataSearch search){
        search.setBid(bid);

        return getList(search);
    }


    //게시글 개별조희
    public BoardData get(Long seq){
        return null;
    }
}
