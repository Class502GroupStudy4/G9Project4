package org.g9project4.mypage.controllers;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;
import org.g9project4.member.constants.Gender;

import java.time.LocalDate;

@Data
public class RequestProfile {

    private String gid;

    @NotBlank
    private String userName;

    private String password;

    private String confirmPassword;

    private String mobile;

    private LocalDate birth;  // 출생일

    private Gender gende;  // 성별 (MALE, FEMALE)

    private Boolean isForeigner;  // 외국인 여부 (외국인 true, 내국인 false)

}
