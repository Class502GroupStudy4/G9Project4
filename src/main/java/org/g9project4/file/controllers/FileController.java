package org.g9project4.file.controllers;

import lombok.RequiredArgsConstructor;
import org.g9project4.file.entities.FileInfo;
import org.g9project4.file.services.FileUploadService;
import org.g9project4.global.exceptions.RestExceptionProcessor;
import org.g9project4.global.rests.JSONData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController implements RestExceptionProcessor {

    private final FileUploadService uploadService;

    @PostMapping("/upload")//파일은 post 형태로 넘어온다
    public <O> ResponseEntity<JSONData<O>> upload(@RequestPart("file") MultipartFile[] files,//어떤 필드 인지 알려주는 역할(name값)
                                                  @RequestParam(name = "gid", required = false) String gid,//form 안에 있는 파라미터
                                                  @RequestParam(name = "location", required = false) String location) {
        List<FileInfo> items = uploadService.upload(files, gid, location);
        HttpStatus status = HttpStatus.CREATED;
        JSONData<O> data = new JSONData<O>(items);
        data.setStatus(status);

        return ResponseEntity.status(status).body(data);
    }
}
