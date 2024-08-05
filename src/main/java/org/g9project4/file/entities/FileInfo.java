package org.g9project4.file.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.g9project4.global.entities.BaseMemberEntity;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo extends BaseMemberEntity {
    @Id
    @GeneratedValue
    private Long seq;// 서버에 업로드 될 파일 이름 - seq.확장자

    @Column(length = 45, nullable = false)
    private String gid = UUID.randomUUID().toString();// 그룹 아이디 Unique Id
    @Column(length = 45)
    private String location; // 그룹 내에 세분화 된 위치 설정

    @Column(length = 80, nullable = false)
    private String fileName;

    @Column(length = 30)
    private String extenstion;//파일 확장자

    @Column(length = 80)
    private String contentType;

    private boolean done; //그룹 작업 완료 여부 (글쓰기 가 끝나야지 저장)

    @Transient
    private String fileUrl;//파일 접근 URL
    @Transient
    private String filePath;//파일 업로드 경로
}
