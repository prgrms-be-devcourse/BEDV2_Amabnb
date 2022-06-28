package com.prgrms.amabnb.security.handler;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.amabnb.security.oauth.OAuthService;
import com.prgrms.amabnb.token.dto.TokenResponse;

@ExtendWith(MockitoExtension.class)
class OAuthAuthenticationSuccessHandlerTest {

    @Mock
    OAuthService oAuthService;
    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    OAuthAuthenticationSuccessHandler handler;

    MockHttpServletRequest request;
    MockHttpServletResponse response;
    OAuth2User oAuth2User;

    @Mock
    Authentication authentication;

    @BeforeEach
    void setUp() {
        handler = new OAuthAuthenticationSuccessHandler(oAuthService, objectMapper);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        oAuth2User = new DefaultOAuth2User(null, createAttributes(), "id");
        given(authentication.getPrincipal()).willReturn(oAuth2User);
    }

    @DisplayName("OAuth2 로그인 성공 handler")
    @Test
    void name() throws Exception {
        request.setRequestURI("login/oauth2/code/kakao");
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        given(oAuthService.register(any())).willReturn(new TokenResponse(accessToken, refreshToken));
        handler.onAuthenticationSuccess(request, response, authentication);

        var tokenResponse = objectMapper.readValue(response.getContentAsString(), TokenResponse.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(tokenResponse.accessToken()).isEqualTo(accessToken);
        assertThat(tokenResponse.refreshToken()).isEqualTo(refreshToken);
    }

    private Map<String, Object> createAttributes() {
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("id", 1L);

        Map<String, String> properties = new LinkedHashMap<>();
        properties.put("nickname", "Armand");
        properties.put("profile_image", "url");

        Map<String, String> kakaoAcount = new LinkedHashMap<>();
        kakaoAcount.put("email", "Armand@prgrms.com");

        attributes.put("properties", properties);
        attributes.put("kakao_account", kakaoAcount);
        return attributes;
    }

}
