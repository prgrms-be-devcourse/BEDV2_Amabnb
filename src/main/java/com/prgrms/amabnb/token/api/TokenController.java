package com.prgrms.amabnb.token.api;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.amabnb.security.util.AuthorizationExtractor;
import com.prgrms.amabnb.token.dto.AccessTokenResponse;
import com.prgrms.amabnb.token.dto.RefreshTokenRequest;
import com.prgrms.amabnb.token.service.TokenService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TokenController {

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
