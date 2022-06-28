package com.prgrms.amabnb.security.oauth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.amabnb.token.dto.TokenResponse;
import com.prgrms.amabnb.token.service.TokenService;
import com.prgrms.amabnb.user.dto.response.UserRegisterResponse;
import com.prgrms.amabnb.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class OAuthServiceTest {

    @InjectMocks
    OAuthService oAuthService;

    @Mock
    TokenService tokenService;

    @Mock
    UserService userService;

    @Nested
    class Register {

        UserProfile givenUserProfile;
        TokenResponse givenToken;
        UserRegisterResponse givenRegisteredUser;

        @BeforeEach
        void MOCK_SET() {
            givenUserProfile = UserProfile.builder()
                .oauthId("something oauth id")
                .provider("kakao")
                .name("subin")
                .email("armand@prgrms.com")
                .profileImgUrl("something imageurl")
                .build();
            givenToken = new TokenResponse("accessToekn", "refreshToken");
            givenRegisteredUser = new UserRegisterResponse(1L, "ROLE_USER");

            doReturn(givenRegisteredUser).when(userService).register(givenUserProfile);
            doReturn(givenToken).when(tokenService).createToken(givenRegisteredUser);
        }

        @Test
        @DisplayName("UserProfile 정보가 들어오면 유저를 등록할 수 있다.")
        void register() {
            // when
            var result = oAuthService.register(givenUserProfile);

            // then : 행위검증
            then(userService).should(times(1)).register(givenUserProfile);
            then(tokenService).should(times(1)).createToken(givenRegisteredUser);

            // then : 값 검증
            assertThat(result.accessToken()).isEqualTo(givenToken.accessToken());
            assertThat(result.refreshToken()).isEqualTo(givenToken.refreshToken());
        }
    }
}
