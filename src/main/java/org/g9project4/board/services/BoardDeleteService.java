package org.g9project4.board.services;

import lombok.RequiredArgsConstructor;
import org.g9project4.board.entities.Board;
import org.g9project4.board.entities.BoardData;
import org.g9project4.board.repositories.BoardDataRepository;
import org.g9project4.file.services.FileDeleteService;
import org.g9project4.global.constants.DeleteStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BoardDeleteService {
    private final BoardInfoService infoService;
    private final FileDeleteService deleteService;
    private final BoardDataRepository repository;

    public BoardData delete(Long seq){
        BoardData data = infoService.get(seq);
        data.setDeletedAt(LocalDateTime.now());
        repository.saveAndFlush(data);
        return data;
    }
    @Transactional
    public BoardData complete(Long seq){
        BoardData data = infoService.get(seq, DeleteStatus.ALL);

        String gid = data.getGid();

        deleteService.delete(gid);
        repository.delete(data);
        repository.flush();
        return data;
    }

    public BoardData recover(Long seq){
        BoardData item = infoService.get(seq);
        item.setDeletedAt(null);
        repository.saveAndFlush(item);
        return item;
    }
}
