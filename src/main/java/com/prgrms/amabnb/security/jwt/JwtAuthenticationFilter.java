package com.prgrms.amabnb.security.jwt;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.prgrms.amabnb.security.util.AuthorizationExtractor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().endsWith("token");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
        throws ServletException, IOException {

        var accessToken = getAccessToken(req);

        if (accessToken != null) {
            var authentication = createAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(req, res);
    }

    private String getAccessToken(HttpServletRequest request) {
        var token = AuthorizationExtractor.extract(request);
        if (token != null) {
            jwtTokenProvider.validateToken(token);
        }
        return token;
    }

    private JwtAuthenticationToken createAuthentication(String accessToken) {
        var claims = jwtTokenProvider.getClaims(accessToken);
        var userId = claims.get("userId", Long.class);
        var role = claims.get("role", String.class);
        return new JwtAuthenticationToken(new JwtAuthentication(accessToken, userId), null, toAuthorities(role));
    }

    private List<GrantedAuthority> toAuthorities(String role) {
        return List.of(new SimpleGrantedAuthority(role));
    }

}
