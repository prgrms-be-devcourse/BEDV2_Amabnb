package com.prgrms.amabnb.common.security.jwt.exception;

import org.springframework.http.HttpStatus;

import com.prgrms.amabnb.common.exception.BusinessException;

public class TokenException extends BusinessException {

    public TokenException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }

}
