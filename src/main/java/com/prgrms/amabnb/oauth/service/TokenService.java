package com.prgrms.amabnb.oauth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.common.security.jwt.JwtTokenProvider;
import com.prgrms.amabnb.common.security.jwt.exception.InvalidTokenException;
import com.prgrms.amabnb.oauth.dto.AccessTokenResponse;
import com.prgrms.amabnb.oauth.dto.RefreshTokenRequest;
import com.prgrms.amabnb.oauth.entity.Token;
import com.prgrms.amabnb.oauth.entity.TokenRepository;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {

    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void saveRefreshToken(String refreshToken, Long userId) {
        tokenRepository.save(new Token(refreshToken, userId));
    }

    @Transactional
    public AccessTokenResponse refreshAccessToken(String accessToken, RefreshTokenRequest refreshTokenRequest) {
        var refreshToken = refreshTokenRequest.getRefreshToken();
        jwtTokenProvider.validateToken(refreshToken);

        Claims claims = jwtTokenProvider.getClaims(accessToken);
        var userId = claims.get("userId", Long.class);
        var findRefreshToken = tokenRepository.findTokenByUserId(userId)
            .map(Token::getRefreshToken)
            .orElseThrow(InvalidTokenException::new);

        if (!refreshToken.equals(findRefreshToken)) {
            throw new InvalidTokenException();
        }

        var role = claims.get("role", String.class);
        var newAccessToken = jwtTokenProvider.createAccessToken(userId, role);
        return new AccessTokenResponse(newAccessToken);
    }

}
