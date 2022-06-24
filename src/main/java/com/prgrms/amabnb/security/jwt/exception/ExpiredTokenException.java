package com.prgrms.amabnb.security.jwt.exception;

import org.springframework.http.HttpStatus;

public class ExpiredTokenException extends TokenException {

    private static final String MESSAGE = "만료된 토큰입니다.";

    public ExpiredTokenException() {
        super(HttpStatus.UNAUTHORIZED, MESSAGE);
    }

}
