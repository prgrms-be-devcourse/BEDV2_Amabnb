package com.prgrms.amabnb.oauth.api;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.amabnb.common.security.jwt.util.AuthorizationExtractor;
import com.prgrms.amabnb.oauth.dto.AccessTokenResponse;
import com.prgrms.amabnb.oauth.dto.RefreshTokenRequest;
import com.prgrms.amabnb.oauth.service.TokenService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final TokenService tokenService;

    @PostMapping("/token")
    public ResponseEntity<AccessTokenResponse> refreshAccessToken(
        HttpServletRequest httpServletRequest,
        @Valid @RequestBody RefreshTokenRequest refreshToken
    ) {
        String accessToken = AuthorizationExtractor.extract(httpServletRequest);
        return ResponseEntity.ok(tokenService.refreshAccessToken(accessToken, refreshToken));
    }

}
