package com.prgrms.amabnb.user.service;

import static com.prgrms.amabnb.config.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.amabnb.user.dto.response.MyUserInfoResponse;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.entity.UserRole;
import com.prgrms.amabnb.user.exception.UserNotFoundException;
import com.prgrms.amabnb.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Nested
    class register {
        @DisplayName("저장된 유저가 없을 경우 유저를 저장하고 유저 정보를 반환한다.")
        @Test
        void register_NotSavedUser() {
            // given
            var userProfile = createUserProfile("test");
            given(userRepository.findByOauthId(any(String.class))).willReturn(Optional.empty());
            given(userRepository.save(any(User.class))).willReturn(createUserWithId("test"));

            // when
            var registerResponse = userService.register(userProfile);

            // then
            assertAll(
                () -> assertThat(registerResponse.id()).isEqualTo(1L),
                () -> assertThat(registerResponse.role()).isEqualTo(UserRole.GUEST.getGrantedAuthority())
            );
        }

        @DisplayName("저장된 유저가 있을 경우 해당 유저 정보를 반환한다.")
        @Test
        void register_SavedUser() {
            // given
            var userProfile = createUserProfile("test");
            given(userRepository.findByOauthId(any(String.class))).willReturn(Optional.of(createUserWithId("test")));

            // when
            var registerResponse = userService.register(userProfile);

            // then
            assertAll(
                () -> assertThat(registerResponse.id()).isEqualTo(1L),
                () -> assertThat(registerResponse.role()).isEqualTo("ROLE_GUEST")
            );
        }
    }

    @Nested
    @DisplayName("유저 정보를 조회할 수 있다 #45")
    class FindUserInfo {
        User givenUser = createUserWithId("test");

        @DisplayName("Principle 에서 userId를 파싱해 유저정보를 db 에서 가져온다")
        @Test
        void parseUserInfoAndFindUserFromDB() {
            // given
            var expected = MyUserInfoResponse.from(givenUser);
            given(userRepository.findById(any(Long.class))).willReturn(Optional.of(givenUser));

            // when
            var userInfo = userService.findUserInfo(givenUser.getId());

            // then
            assertAll(
                () -> assertThat(userInfo.name()).isEqualTo(expected.name()),
                () -> assertThat(userInfo.email()).isEqualTo(expected.email()),
                () -> assertThat(userInfo.profileImageUrl()).isEqualTo(expected.profileImageUrl()),
                () -> assertThat(userInfo.role()).isEqualTo(expected.role())
            );
        }

        @DisplayName("Principal 에서 userId 에 해당하는 유저가 없으면 UserNotFound Exception")
        @Test
        void noMatchUser() {
            given(userRepository.findById(any(Long.class))).willReturn(Optional.empty()); // userNotFound
            var illegalId = 123L;
            assertThatThrownBy(() -> userService.findUserInfo(illegalId))
                .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("유저는 서비스 탈퇴를 할 수 있다 #46")
    class ExitUser {
        User givenUser = createUserWithId("test");

        @DisplayName("유저가 탈퇴하면 해당하는 유저 정보를 파기한다")
        @Test
        void exitUser() {
            var response = userService.deleteUserAccount(givenUser.getId());
            assertThat(response).isEqualTo("delete success : " + givenUser.getId());
        }
    }
}
