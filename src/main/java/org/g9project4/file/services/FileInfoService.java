package org.g9project4.file.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.g9project4.file.constants.FileStatus;
import org.g9project4.file.entities.FileInfo;
import org.g9project4.file.exceptions.FileNotFoundException;
import org.g9project4.file.repositories.FileInfoRepository;
import org.g9project4.global.configs.FileProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class)
public class FileInfoService {
    private final FileInfoRepository infoRepository;
    private final FileProperties properties;
    private final HttpServletRequest request;

    /**
     * 파일 1개 조회
     *
     * @param seq : 파일 등록 번호
     * @return
     */
    public FileInfo get(Long seq) {
        FileInfo item = infoRepository.findById(seq).orElseThrow(FileNotFoundException::new);
        /**
         * 2차 가공
         * 1. 파일을 접근 할 수 있는 URL -
         * 2. 파일을 접근 할 수 있는 경로 PATH
         */
        addFileInfo(item);
        return item;
    }

    /**
     * 파일 목록 조회
     *
     * @param gid
     * @param location
     * @param status   : ALL : 완료+미완료, DONE - 완료, UNDONE - 미완료
     * @return
     */
    public List<FileInfo> getList(String gid, String location, FileStatus status) {
        return null;
    }

    /**
     * 파일 정보 추가 처리
     * -fileUrl, filePath
     *
     * @param item
     */
    public void addFileInfo(FileInfo item) {//파일 정보 추가(url, path)
        String fileUrl = getFileUrl(item);
        String filepath = getFilePath(item);

        item.setFileUrl(fileUrl);
        item.setFilePath(filepath);
    }

    // 브라우저 접근 주소
    public String getFileUrl(FileInfo item) {
        return request.getContextPath() + properties.getUrl() + "/" + getFolder(item.getSeq()) + "/" + getFileName(item);
    }

    //서버 업로드 경로
    public String getFilePath(FileInfo item) {
        return properties.getPath() + "/" + getFolder(item.getSeq()) + "/" + getFileName(item);
    }

    public String getFolder(long seq) {
        return String.valueOf(seq % 10L);
    }

    public String getFileName(FileInfo item) {
        String fileName = item.getSeq() + Objects.requireNonNullElse(item.getExtenstion(), "");
        return fileName;
    }
}
