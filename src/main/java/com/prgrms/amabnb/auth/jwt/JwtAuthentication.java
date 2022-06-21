package com.prgrms.amabnb.auth.jwt;

import java.util.Objects;

public record JwtAuthentication(String token, String userId) {

    public JwtAuthentication {
        validateToken(token);
        validateUserId(userId);
    }

    private void validateToken(String token) {
        if (Objects.isNull(token) || token.isBlank()) {
            throw new IllegalArgumentException();
        }
    }

    private void validateUserId(String userId) {
        if (Objects.isNull(userId) || userId.isBlank()) {
            throw new IllegalArgumentException();
        }
    }

}
