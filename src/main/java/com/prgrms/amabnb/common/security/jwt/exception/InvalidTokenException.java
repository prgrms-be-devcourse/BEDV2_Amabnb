package com.prgrms.amabnb.common.security.jwt.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends TokenException {

    private static final String MESSAGE = "유효하지 않은 토큰입니다.";

    public InvalidTokenException() {
        super(HttpStatus.UNAUTHORIZED, MESSAGE);
    }

}
