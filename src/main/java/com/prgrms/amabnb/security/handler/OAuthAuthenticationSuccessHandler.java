package com.prgrms.amabnb.security.handler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.amabnb.oauth.OAuthProvider;
import com.prgrms.amabnb.oauth.OAuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuthService oauthService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        var providerName = parseProviderName(request);
        var principal = authentication.getPrincipal();

        if (principal instanceof OAuth2User oauth) {

            var userProfile = OAuthProvider
                .getProviderFromName(providerName)
                .toUserProfile(oauth);

            var tokenResponse = oauthService.register(userProfile);

            response.setStatus(HttpStatus.OK.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
        }
    }

    private String parseProviderName(HttpServletRequest request) {
        var splitURI = request.getRequestURI().split("/");
        return splitURI[splitURI.length - 1];
    }

}
