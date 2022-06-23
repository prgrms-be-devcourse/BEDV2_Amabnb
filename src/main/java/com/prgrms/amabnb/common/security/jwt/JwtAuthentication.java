package com.prgrms.amabnb.common.security.jwt;

import java.util.Objects;

import com.prgrms.amabnb.common.security.jwt.exception.InvalidTokenException;

public record JwtAuthentication(String token, Long id) {

    public static final long MIN_ID_VALUE = 1L;

    public JwtAuthentication {
        validateToken(token);
        validateUserId(id);
    }

    private void validateToken(String token) {
        if (Objects.isNull(token) || token.isBlank()) {
            throw new InvalidTokenException();
        }
    }

    private void validateUserId(Long id) {
        if (id == null || id < MIN_ID_VALUE) {
            throw new InvalidTokenException();
        }
    }

}
