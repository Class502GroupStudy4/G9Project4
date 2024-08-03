package org.g9project4.file.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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

    @Column(length = 80)
    private String contentType;

    private boolean done; //그룹 작업 완료 여부 (글쓰기 가 끝나야지 저장)

}
