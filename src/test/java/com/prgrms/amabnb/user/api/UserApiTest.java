package com.prgrms.amabnb.user.api;

import static com.prgrms.amabnb.config.util.Fixture.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

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

    @Nested
    @DisplayName("유저는 본인의 정보를 확인할 수 있다 #45")
    class MyPage {
        @Test
        void userPage() throws Exception {
            mockMvc.perform(get("/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken.accessToken()))
                .andExpect(handler().methodName("myPage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(givenUser.getName()))
                .andExpect(jsonPath("email").value(givenUser.getEmail().getValue()))
                .andExpect(jsonPath("profileImageUrl").value(givenUser.getProfileImgUrl()))
                .andExpect(jsonPath("role").value(givenUser.getUserRole().getGrantedAuthority()))
                .andDo(print());
        }
    }

    @Nested
    @DisplayName("유저는 서비스 탈퇴를 할 수 있다 #46")
    class DeleteAccount {
        @Test
        void deleteUser() throws Exception {
            mockMvc.perform(delete("/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken.accessToken()))
                .andExpect(handler().methodName("deleteAccount"))
                .andExpect(status().isNoContent())
                .andDo(print());
        }
    }
}
