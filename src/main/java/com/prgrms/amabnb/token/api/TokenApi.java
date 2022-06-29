package com.prgrms.amabnb.token.api;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.amabnb.security.jwt.JwtAuthentication;
import com.prgrms.amabnb.security.util.AuthorizationExtractor;
import com.prgrms.amabnb.token.dto.AccessTokenResponse;
import com.prgrms.amabnb.token.dto.RefreshTokenRequest;
import com.prgrms.amabnb.token.service.TokenService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TokenApi {

    private final TokenService tokenService;

    @PostMapping("/tokens")
    public ResponseEntity<AccessTokenResponse> refreshAccessToken(
        HttpServletRequest httpServletRequest,
        @Valid @RequestBody RefreshTokenRequest refreshToken
    ) {
        String accessToken = AuthorizationExtractor.extract(httpServletRequest);
        return ResponseEntity.ok(tokenService.refreshAccessToken(accessToken, refreshToken));
    }

    @DeleteMapping("/tokens")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRefreshToken(@AuthenticationPrincipal JwtAuthentication user) {
        tokenService.deleteTokenByUserId(user.id());
    }
}
