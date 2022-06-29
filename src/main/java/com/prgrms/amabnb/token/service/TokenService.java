package com.prgrms.amabnb.token.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.security.jwt.JwtTokenProvider;
import com.prgrms.amabnb.security.jwt.exception.InvalidTokenException;
import com.prgrms.amabnb.token.dto.AccessTokenResponse;
import com.prgrms.amabnb.token.dto.RefreshTokenRequest;
import com.prgrms.amabnb.token.dto.TokenResponse;
import com.prgrms.amabnb.token.entity.Token;
import com.prgrms.amabnb.token.repository.TokenRepository;
import com.prgrms.amabnb.user.dto.response.UserRegisterResponse;
import com.prgrms.amabnb.user.exception.UserNotFoundException;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {

    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenResponse createToken(UserRegisterResponse user) {
        var accessToken = jwtTokenProvider.createAccessToken(user.id(), user.role());
        var refreshToken = jwtTokenProvider.createRefreshToken();
        tokenRepository.save(new Token(refreshToken, user.id()));
        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public AccessTokenResponse refreshAccessToken(String accessToken, RefreshTokenRequest refreshTokenRequest) {
        jwtTokenProvider.validateAccessToken(accessToken);

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

    @Transactional
    public void deleteTokenByUserId(Long userId) {
        if (!tokenRepository.existsByUserId(userId)) {
            throw new UserNotFoundException();
        }
        tokenRepository.deleteByUserId(userId);
    }
}
