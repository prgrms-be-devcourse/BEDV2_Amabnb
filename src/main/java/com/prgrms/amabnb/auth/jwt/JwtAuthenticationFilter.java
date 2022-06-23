package com.prgrms.amabnb.auth.jwt;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.prgrms.amabnb.auth.exception.TokenException;
import com.prgrms.amabnb.auth.service.TokenService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String ACCESS_TOKEN_KEY_NAME = "accessToken";
    private static final String REFRESH_TOKEN_KEY_NAME = "refreshToken";

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws
        ServletException,
        IOException {

        var accessToken = getAccessToken(req, res);
        var authentication = createAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(req, res);
    }

    private JwtAuthenticationToken createAuthentication(String accessToken) {
        String userId = jwtTokenProvider.getPayload(accessToken);
        if (userId == null || userId.isBlank()) {
            throw new TokenException("잘못된 토큰입니다.");
        }
        return new JwtAuthenticationToken(new JwtAuthentication(accessToken, userId), null, Collections.emptyList());
    }

    private String getAccessToken(HttpServletRequest request, HttpServletResponse res) {
        String accessToken = "";
        try {
            accessToken = getToken(request.getCookies(), ACCESS_TOKEN_KEY_NAME);
            jwtTokenProvider.validateToken(accessToken);
            return accessToken;
        } catch (ExpiredJwtException e) {
            if (tokenService.validRefreshToken(getRefreshToken(request))) {
                String userId = jwtTokenProvider.getPayload(accessToken);
                accessToken = jwtTokenProvider.createAccessToken(userId);
                res.addCookie(new Cookie(ACCESS_TOKEN_KEY_NAME, accessToken));
                return accessToken;
            }
            throw new TokenException("잘못된 refreshToken 값입니다.");
        } catch (JwtException | IllegalArgumentException exception) {
            throw new TokenException("잘못된 accessToken 값입니다.");
        }
    }

    private String getRefreshToken(HttpServletRequest request) {
        try {
            String refreshToken = getToken(request.getCookies(), REFRESH_TOKEN_KEY_NAME);
            jwtTokenProvider.validateToken(refreshToken);
            return refreshToken;
        } catch (ExpiredJwtException e) {
            throw new TokenException("재로그인을 해주세요.");
        } catch (JwtException | IllegalArgumentException e) {
            throw new TokenException("잘못된 토큰입니다.");
        }
    }

    private String getToken(Cookie[] cookies, String key) {
        if (cookies == null) {
            throw new TokenException("토큰이 없습니다.");
        }
        return Arrays.stream(cookies)
            .filter(cookie -> cookie.getName().equals(key))
            .findFirst()
            .map(Cookie::getValue)
            .orElseThrow(() -> new TokenException(key + "가 없습니다"));
    }

}
