package org.g9project4.file.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.g9project4.file.entities.FileInfo;
import org.g9project4.file.services.FileUploadService;
import org.g9project4.global.exceptions.RestExceptionProcessor;
import org.g9project4.global.rests.JSONData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController implements RestExceptionProcessor {

    private final FileUploadService uploadService;

    @PostMapping("/upload")//파일은 post 형태로 넘어온다
    public ResponseEntity<JSONData> upload(@RequestPart("file") MultipartFile[] files,//어떤 필드 인지 알려주는 역할(name값)
                                           @Valid RequestUpload form, Errors errors) {

        if (errors.hasErrors()) {

        }

        List<FileInfo> items = uploadService.upload(files, form.getGid(), form.getLocation());
        HttpStatus status = HttpStatus.CREATED;
        JSONData data = new JSONData(items);
        data.setStatus(status);

        return ResponseEntity.status(status).body(data);
    }
}
