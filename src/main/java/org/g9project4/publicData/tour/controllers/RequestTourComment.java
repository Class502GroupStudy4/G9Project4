package org.g9project4.publicData.tour.controllers;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestTourComment {
    private String mode = "add";//기본 댓글 쓰기

    private Long seq;//댓글 등록번호

    private Long contentId;

    @NotBlank
    private String commenter;// 작성자

    private String guestPw; //비회원 비밀번호

    @NotBlank
    private String content;//댓글 내용
}
