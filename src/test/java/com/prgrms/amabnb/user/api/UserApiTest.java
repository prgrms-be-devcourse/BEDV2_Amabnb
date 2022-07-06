package com.prgrms.amabnb.user.api;

import static com.prgrms.amabnb.config.util.Fixture.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.JsonFieldType;

import com.prgrms.amabnb.config.ApiTest;
import com.prgrms.amabnb.token.dto.TokenResponse;
import com.prgrms.amabnb.token.service.TokenService;
import com.prgrms.amabnb.user.dto.response.UserRegisterResponse;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.repository.UserRepository;
import com.prgrms.amabnb.user.service.UserService;

class UserApiTest extends ApiTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenService tokenService;

    TokenResponse givenToken;
    User givenUser;

    @BeforeEach
    void setUp() {
        givenUser = userRepository.save(createUser("test"));
        givenToken = tokenService.createToken(
            new UserRegisterResponse(givenUser.getId(), givenUser.getUserRole().getGrantedAuthority()));
    }

    @DisplayName("카카오 로그인 요청 시 카카오 로그인 창을 응답한다.")
    @Test
    void loginDocument() throws Exception {
        // when
        mockMvc.perform(post("/oauth2/authorization/kakao"))
            .andExpect(status().is3xxRedirection())

            // then
            .andDo(document.document(
            ));
    }

    @Nested
    @DisplayName("유저는 본인의 정보를 확인할 수 있다 #45")
    class MyPage {
        @Test
        void userPage() throws Exception {
            // when
            mockMvc.perform(get("/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken.accessToken()))

                // then
                .andExpectAll(
                    handler().methodName("myPage"),
                    status().isOk(),
                    jsonPath("data.name").value(givenUser.getName()),
                    jsonPath("data.profileImageUrl").value(givenUser.getProfileImgUrl()),
                    jsonPath("data.role").value(givenUser.getUserRole().getGrantedAuthority())
                )

                // docs
                .andDo(document.document(
                    responseFields(
                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("이름"),
                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지"),
                        fieldWithPath("data.role").type(JsonFieldType.STRING).description("회원 권한")
                    )));
        }
    }

    @Nested
    @DisplayName("유저는 서비스 탈퇴를 할 수 있다 #46")
    class DeleteAccount {
        @Test
        void deleteUser() throws Exception {
            mockMvc.perform(delete("/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken.accessToken()))

                .andExpectAll(
                    handler().methodName("deleteAccount"),
                    status().isNoContent()
                )
                .andDo(document.document(
                    tokenRequestHeader()
                ));
        }
    }
}
