package com.prgrms.amabnb.oauth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.amabnb.oauth.dto.AccessTokenResponse;
import com.prgrms.amabnb.oauth.dto.RefreshTokenRequest;
import com.prgrms.amabnb.oauth.dto.TokenResponse;
import com.prgrms.amabnb.oauth.entity.Token;
import com.prgrms.amabnb.oauth.repository.TokenRepository;
import com.prgrms.amabnb.security.jwt.JwtTokenProvider;
import com.prgrms.amabnb.security.jwt.exception.InvalidTokenException;
import com.prgrms.amabnb.user.dto.response.UserRegisterResponse;

import io.jsonwebtoken.impl.DefaultClaims;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private TokenService tokenService;

    @DisplayName("accessToken과 refreshToken을 생성한다.")
    @Test
    void createToken() {
        // given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        given(jwtTokenProvider.createAccessToken(any(Long.class), any(String.class))).willReturn(accessToken);
        given(jwtTokenProvider.createRefreshToken()).willReturn(refreshToken);

        // when
        TokenResponse tokenResponse = tokenService.createToken(new UserRegisterResponse(1L, "ROLE_GUEST"));

        // then
        assertThat(tokenResponse.accessToken()).isEqualTo(accessToken);
        assertThat(tokenResponse.refreshToken()).isEqualTo(refreshToken);
    }

    @DisplayName("accessToken을 재생성한다.")
    @Test
    void refreshAccessToken() {
        // given
        long userId = 1L;
        String accessToken = "accessToken";
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("refreshToken");
        willDoNothing().given(jwtTokenProvider).validateToken(any(String.class));
        given(jwtTokenProvider.getClaims(any(String.class))).willReturn(mockClaims(userId));
        given(tokenRepository.findTokenByUserId(any(Long.class))).willReturn(createToken(userId));
        given(jwtTokenProvider.createAccessToken(any(Long.class), any(String.class))).willReturn("newAccessToken");

        // when
        AccessTokenResponse response = tokenService.refreshAccessToken(accessToken, refreshTokenRequest);

        // then
        assertThat(response.accessToken()).isEqualTo("newAccessToken");
    }

    @DisplayName("유효하지 않은 refreshToken이라면 예외를 발생한다.")
    @Test
    void refreshAccessToken_invalidRefreshToken() {
        // given
        String accessToken = "accessToken";
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("invalid");
        willThrow(InvalidTokenException.class).given(jwtTokenProvider).validateToken(any(String.class));

        // when
        // then
        assertThatThrownBy(() -> tokenService.refreshAccessToken(accessToken, refreshTokenRequest))
            .isInstanceOf(InvalidTokenException.class);
    }

    @DisplayName("저장되있지 않은 refreshToken이라면 예외를 발생한다.")
    @Test
    void refreshAccessToken_notFoundToken() {
        // given
        long userId = 1L;
        String accessToken = "accessToken";
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("invalid");
        given(jwtTokenProvider.getClaims(any(String.class))).willReturn(mockClaims(userId));
        given(tokenRepository.findTokenByUserId(any(Long.class))).willReturn(
            Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> tokenService.refreshAccessToken(accessToken, refreshTokenRequest))
            .isInstanceOf(InvalidTokenException.class)
            .hasMessage("유효하지 않은 토큰입니다.");
    }

    @DisplayName("저장되어 있는 refreshToken과 다르다면 예외를 발생한다.")
    @Test
    void refreshAccessToken_differentToken() {
        // given
        long userId = 1L;
        String accessToken = "accessToken";
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("invalid");
        given(jwtTokenProvider.getClaims(any(String.class))).willReturn(mockClaims(userId));
        given(tokenRepository.findTokenByUserId(any(Long.class))).willReturn(createToken(1L));

        // when
        // then
        assertThatThrownBy(() -> tokenService.refreshAccessToken(accessToken, refreshTokenRequest))
            .isInstanceOf(InvalidTokenException.class)
            .hasMessage("유효하지 않은 토큰입니다.");
    }

    @DisplayName("accessToken이 유효하지 않은 토큰이라면 예외를 발생한다. ")
    @Test
    void refreshAccessToken_InvalidAccessToken() {
        // given
        String invalidAccessToken = "Invalid";
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("refreshToken");
        willThrow(InvalidTokenException.class).given(jwtTokenProvider).validateAccessToken(invalidAccessToken);

        // when
        // then
        assertThatThrownBy(() -> tokenService.refreshAccessToken(invalidAccessToken, refreshTokenRequest))
            .isInstanceOf(InvalidTokenException.class);
    }

    private DefaultClaims mockClaims(long userId) {
        return new DefaultClaims(Map.of("userId", userId, "role", "ROLE_HOST"));
    }

    private Optional<Token> createToken(long userId) {
        return Optional.of(new Token(1L, "refreshToken", userId));
    }

}
