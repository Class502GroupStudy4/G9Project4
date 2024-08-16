package org.g9project4.board.services;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.g9project4.board.entities.BoardData;
import org.g9project4.board.repositories.BoardDataRepository;
import org.g9project4.global.ListData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardInfoService {
    private final JPAQueryFactory jpaQueryFactory;
    private final BoardDataRepository repository;
    public ListData<BoardData> getList(){
        return null;
    }
    //게시글 개별조희
    public BoardData get(Long seq){
        return null;
    }
}
