package com.prgrms.amabnb.security.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.prgrms.amabnb.security.jwt.exception.ExpiredTokenException;
import com.prgrms.amabnb.security.jwt.exception.InvalidTokenException;

import io.jsonwebtoken.Claims;

class JwtTokenProviderTest {

    private static final JwtTokenProvider PROVIDER = new JwtTokenProvider(
        "test",
        "IbPja88BzwyzmvvNwOadW8JUZF5MX1vzxfFtlvokPNE=",
        60_000,
        60_000
    );

    @DisplayName("토큰에서 페이로드를 추출한다.")
    @Test
    void getClaims() {
        // given
        long userId = 1L;
        String role = "role";
        String token = PROVIDER.createAccessToken(userId, role);

        // when
        Claims claims = PROVIDER.getClaims(token);

        // then
        assertAll(
            () -> assertThat(claims.get("userId", Long.class)).isEqualTo(userId),
            () -> assertThat(claims.get("role", String.class)).isEqualTo(role)
        );
    }

    @DisplayName("refreshToken을 생성한다.")
    @Test
    void createRefreshToken() {
        // given
        String refreshToken = PROVIDER.createRefreshToken();

        // when
        String payload = PROVIDER.getClaims(refreshToken)
            .getSubject();

        // then
        assertThat(payload).isNotNull();
    }

    @DisplayName("토큰의 유효성 검사를 한다.")
    @Test
    void validateToken() {
        // given
        String token = PROVIDER.createAccessToken(1L, "role");

        // when
        // then
        assertDoesNotThrow(() -> PROVIDER.validateToken(token));
    }

    @DisplayName("토큰의 만료시간이 지나면 예외를 발생한다.")
    @Test
    void validateToken_Expired() {
        // given
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(
            "test",
            "IbPja88BzwyzmvvNwOadW8JUZF5MX1vzxfFtlvokPNE=",
            0,
            0
        );
        String token = jwtTokenProvider.createAccessToken(1L, "role");

        // when
        // then
        assertThatThrownBy(() -> jwtTokenProvider.validateToken(token))
            .isInstanceOf(ExpiredTokenException.class)
            .hasMessage("만료된 토큰입니다.");
    }

    @DisplayName("유효하지 않은 토큰 검증시 예외를 발생한다.")
    @Test
    void validateToken_Invalid() {
        // given
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(
            "test",
            "IbPja88BzwyzmvvNwOadW8JUZF5MX1vzxfFtlvokPNE=",
            0,
            0
        );
        String token = "Invalid";

        // when
        // then
        assertThatThrownBy(() -> PROVIDER.validateToken(token))
            .isInstanceOf(InvalidTokenException.class)
            .hasMessage("유효하지 않은 토큰입니다.");
    }

    @DisplayName("올바르지 않은 시그니처로 검증 시 예외를 발생한다.")
    @Test
    void validateToken_InvalidSign() {
        // given
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(
            "test",
            "IbPja88BzwyzmvvNwOadW8JUZF5MX1vzxfStlvokPNE=",
            0,
            0
        );
        String token = PROVIDER.createAccessToken(1L, "role");

        // when
        // then
        assertThatThrownBy(() -> jwtTokenProvider.validateToken(token))
            .isInstanceOf(InvalidTokenException.class)
            .hasMessage("유효하지 않은 토큰입니다.");
    }

    @DisplayName("accessToken의 기간이 만료되어도 예외를 던지지 않는다.")
    @Test
    void validateAccessToken() {
        // given
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(
            "test",
            "IbPja88BzwyzmvvNwOadW8JUZF5MX1vzxfFtlvokPNE=",
            0,
            0
        );
        String token = jwtTokenProvider.createAccessToken(1L, "role");

        // when
        // then
        assertDoesNotThrow(() -> jwtTokenProvider.validateAccessToken(token));
    }
}
