package org.g9project4.global.advices;

import lombok.RequiredArgsConstructor;
import org.g9project4.file.entities.FileInfo;
import org.g9project4.global.Utils;
import org.g9project4.global.exceptions.CommonException;
import org.g9project4.global.rests.JSONData;
import org.g9project4.member.MemberUtil;
import org.g9project4.member.entities.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Map;

@ControllerAdvice("org.g9project4")// 범위 설정
@RequiredArgsConstructor
public class CommonControllerAdvice {//전역에서 확인 가능

    private final MemberUtil memberUtil;
    private final Utils utils;

    @ModelAttribute("loggedMember")
    public Member loggedMember(){
        return memberUtil.getMember();
    }
    @ModelAttribute("isLogin")
    public boolean isLogin(){
        return memberUtil.isLogin();
    }
    @ModelAttribute("isAdmin")
    public boolean isAdmin(){
        return memberUtil.isAdmin();
    }

    @ModelAttribute("myProfileImage")
    public FileInfo myProfileImage() {

        return isLogin() ? memberUtil.getMember().getProfileImage() : null;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JSONData> errorHandelr(Exception e) {

        Object message = e.getMessage();

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 500
        if (e instanceof CommonException commonException) {
            status = commonException.getStatus();
            // 에러 코드인 경우는 메세지 조회
            if (commonException.isErrorCode()) message = utils.getMessage(e.getMessage());

            Map<String, List<String>> errorMessages = commonException.getErrorMessages();
            if (errorMessages != null) message = errorMessages;
        }

        JSONData data = new JSONData();
        data.setSuccess(false);
        data.setMessage(message);
        data.setStatus(status);

        e.printStackTrace();

        return ResponseEntity.status(status).body(data);
    }
}
