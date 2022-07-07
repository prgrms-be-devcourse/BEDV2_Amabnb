package com.prgrms.amabnb.token.api;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.prgrms.amabnb.config.ApiTest;
import com.prgrms.amabnb.token.dto.RefreshTokenRequest;
import com.prgrms.amabnb.token.dto.TokenResponse;
import com.prgrms.amabnb.token.service.TokenService;
import com.prgrms.amabnb.user.dto.response.UserRegisterResponse;

class TokenApiTest extends ApiTest {

    @Autowired
    TokenService tokenService;

    TokenResponse givenToken;

    @BeforeEach
    public void createGivenToken() {
        givenToken = tokenService.createToken(new UserRegisterResponse(1L, "ROLE_GUEST"));
    }

    @Nested
    @DisplayName("유저가 가지고 있는 refresh 토큰과 일치하면, access 토큰을 재발급 할 수 있다")
    class refreshAccessToken {

        @Test
        @DisplayName("access 토큰에 있는 정보를 기반으로 새 access 토큰을 발급해준다")
        void refreshAccessToken_success() throws Exception {
            mockMvc.perform(post("/tokens")
                    .with(oauth2Login())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken.accessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new RefreshTokenRequest(givenToken.refreshToken()))))

                // then
                .andExpectAll(
                    handler().methodName("refreshAccessToken"),
                    status().isOk(),
                    jsonPath("data.accessToken").exists()
                )

                // docs
                .andDo(document.document(
                    tokenRequestHeader(),
                    requestFields(
                        fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("refresh Token")
                    ),
                    responseFields(
                        fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("access Token")
                    )
                ));
        }

        @Test
        @DisplayName("유저의 access 토큰이 유효하지 않으면, InvalidToken Exception - unAuthorized")
        void refreshAccessToken_invalidTokenException() throws Exception {
            var illegalToken = "illegal~";

            mockMvc.perform(post("/tokens")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + illegalToken)
                    .contentType(MediaType.APPLICATION_JSON).content(toJson(givenToken.refreshToken())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message", "유효하지 않은 토큰입니다.").exists())
                .andDo(print());
        }

        @ParameterizedTest
        @DisplayName("refresh token은 비어있을 수 없습니다")
        @NullAndEmptySource
        void refreshTokenRequestDto_validation(String value) throws Exception {
            var request = new RefreshTokenRequest(value);
            mockMvc.perform(post("/tokens")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken.accessToken())
                    .contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
                .andExpect(handler().methodName("refreshAccessToken"))
                .andExpect(status().isBadRequest())
                .andDo(print());
        }
    }

    @Nested
    @DisplayName("유저가 로그아웃을 하면, Refresh 토큰을 지운다 #58")
    class deleteRefreshToken {
        @Test
        void deleteRefreshToken() throws Exception {
            // given
            mockMvc.perform(delete("/tokens")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken.accessToken()))

                // then
                .andExpectAll(
                    handler().methodName("deleteRefreshToken"),
                    status().isNoContent()
                )

                //docs
                .andDo(document.document(
                    tokenRequestHeader()
                ));
        }
    }
}
