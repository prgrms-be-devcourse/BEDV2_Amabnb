package com.prgrms.amabnb.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.prgrms.amabnb.security.jwt.exception.ExpiredTokenException;
import com.prgrms.amabnb.security.jwt.exception.InvalidTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    private final String issuer;
    private final String secretKey;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(
        @Value("${jwt.issuer}") String issuer,
        @Value("${jwt.secret-key}") String secretKey,
        @Value("${jwt.access-token.expire-length}") long accessTokenValidityInMilliseconds,
        @Value("${jwt.refresh-token.expire-length}") long refreshTokenValidityInMilliseconds) {
        this.issuer = issuer;
        this.secretKey = secretKey;
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
    }

    public String createAccessToken(long payload, String role) {
        Map<String, Object> claims = Map.of("userId", payload, "role", role);
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        return Jwts.builder()
            .setIssuer(issuer)
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiredDate)
            .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
            .compact();
    }

    public String createRefreshToken() {
        String payload = UUID.randomUUID().toString();
        Claims claims = Jwts.claims().setSubject(payload);
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

        return Jwts.builder()
            .setIssuer(issuer)
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiredDate)
            .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
            .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public void validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }

    public void validateAccessToken(String accessToken) {
        try {
            validateToken(accessToken);
        } catch (ExpiredTokenException ignored) {
        }
    }

}
