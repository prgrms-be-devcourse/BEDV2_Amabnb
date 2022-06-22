package com.prgrms.amabnb.security.jwt;

import java.util.Objects;

public record JwtAuthentication(String token, long id) {

    public JwtAuthentication {
        validateToken(token);
        validateUserId(id);
    }

    private void validateToken(String token) {
        if (Objects.isNull(token) || token.isBlank()) {
            throw new IllegalArgumentException();
        }
    }

    private void validateUserId(long id) {
        if (id < 1L) {
            throw new IllegalArgumentException();
        }
    }

}
