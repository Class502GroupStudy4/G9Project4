package org.g9project4.mypage.controllers;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestProfile {

    private String gid;

    @NotBlank
    private String userName;

    private String password;

    private String confirmPassword;

    private String mobile;

}
