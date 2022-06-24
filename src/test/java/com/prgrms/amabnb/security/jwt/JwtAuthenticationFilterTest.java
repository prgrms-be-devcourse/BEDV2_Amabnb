package com.prgrms.amabnb.security.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import com.prgrms.amabnb.security.jwt.exception.ExpiredTokenException;
import com.prgrms.amabnb.security.jwt.exception.InvalidTokenException;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private JwtTokenProvider jwtTokenProvider;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        jwtTokenProvider = createTokenProvider(60_000, 60_000);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider);
    }

    @DisplayName("Jwt 토큰으로 Authentication을 생성한다.")
    @Test
    void doFilterInternal() throws ServletException, IOException {
        // given
        Long userId = 1L;
        String token = jwtTokenProvider.createAccessToken(userId, "ROLE_GUEST");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer" + token);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        JwtAuthentication jwtAuthentication = getAuthentication();
        assertAll(
            () -> assertThat(jwtAuthentication.id()).isEqualTo(userId),
            () -> assertThat(jwtAuthentication.token()).isEqualTo(token)
        );
    }

    @DisplayName("유효하지 않은 토큰일 경우 예외를 발생한다.")
    @Test
    void doFilterInternal_InvalidToken() {
        // given
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer Invalid");

        // when
        // then
        assertThatThrownBy(() -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain))
            .isInstanceOf(InvalidTokenException.class)
            .hasMessage("유효하지 않은 토큰입니다.");
    }

    @DisplayName("만료된 토큰일 경우 예외를 발생한다.")
    @Test
    void doFilterInternal_ExpiredToken() {
        // given
        JwtTokenProvider jwtTokenProvider = createTokenProvider(0, 0);
        String token = jwtTokenProvider.createAccessToken(1L, "ROLE_GUEST");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer" + token);

        // when

        // then
        assertThatThrownBy(() -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain))
            .isInstanceOf(ExpiredTokenException.class)
            .hasMessage("만료된 토큰입니다.");
    }

    private JwtTokenProvider createTokenProvider(long accessTokenTime, long refreshTokenTime) {
        return new JwtTokenProvider(
            "test",
            "IbPja88BzwyzmvvNwOadW8JUZF5MX1vzxfFtlvokPNE=",
            accessTokenTime,
            refreshTokenTime
        );
    }

    private JwtAuthentication getAuthentication() {
        return (JwtAuthentication)SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal();
    }

}
