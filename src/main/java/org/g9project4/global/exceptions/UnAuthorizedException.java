package org.g9project4.global.exceptions;

import org.springframework.http.HttpStatus;

public class UnAuthorizedException extends CommonException{
    public UnAuthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public UnAuthorizedException() {
        this("UnAuthorized");
        setErrorCode(true);
    }
}
