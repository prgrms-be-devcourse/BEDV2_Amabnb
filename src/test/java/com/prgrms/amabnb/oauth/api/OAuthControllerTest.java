package com.prgrms.amabnb.oauth.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.amabnb.oauth.dto.RefreshTokenRequest;
import com.prgrms.amabnb.oauth.dto.TokenResponse;
import com.prgrms.amabnb.oauth.service.TokenService;
import com.prgrms.amabnb.user.dto.response.UserRegisterResponse;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OAuthControllerTest {

    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    TokenResponse token;

    @Autowired
    TokenService tokenService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .alwaysDo(print())
            .build();
    }

    @BeforeEach
    public void createGivenToken() {
        token = tokenService.createToken(new UserRegisterResponse(1L, "ROLE_GUEST"));
    }

    String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    @Nested
    @DisplayName("유저가 가지고 있는 refresh 토큰과 일치하면, access 토큰을 재발급 할 수 있다")
    class refreshAccessToken {

        @Test
        @DisplayName("access 토큰에 있는 정보를 기반으로 새 access 토큰을 발급해준다")
        void refreshAccessToken_success() throws Exception {
            mockMvc.perform(post("/token")
                    .with(oauth2Login())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                    .contentType(MediaType.APPLICATION_JSON).content(toJson(token.refreshToken())))
                .andExpect(handler().methodName("refreshAccessToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("accessToken").exists())
                .andDo(print());
        }

        @Test
        @DisplayName("유저의 access 토큰이 유효하지 않으면, InvalidToken Exception - unAuthorized")
        void refreshAccessToken_invalidTokenException() throws Exception {
            var illegalToken = "illegal~";

            mockMvc.perform(post("/token")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + illegalToken)
                    .contentType(MediaType.APPLICATION_JSON).content(toJson(token.refreshToken())))
                .andExpect(handler().methodName("refreshAccessToken"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message", "유효하지 않은 토큰입니다.").exists())
                .andDo(print());
        }

        @ParameterizedTest
        @DisplayName("refresh token은 비어있을 수 없습니다")
        @NullAndEmptySource
        void refreshTokenRequestDto_validation(String value) throws Exception {
            var request = new RefreshTokenRequest(value);
            mockMvc.perform(post("/token")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                    .contentType(MediaType.APPLICATION_JSON).content(toJson(request)))

                .andExpect(handler().methodName("refreshAccessToken"))
                // TODO : handler 지정하기
                // .andExpect(status().isBadRequest())
                //.methodArgumentException - expected
                .andDo(print());
        }
    }

}
