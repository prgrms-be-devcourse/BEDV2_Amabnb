package com.prgrms.amabnb.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.amabnb.oauth.dto.UserProfile;
import com.prgrms.amabnb.user.dto.response.UserRegisterResponse;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.entity.UserRole;
import com.prgrms.amabnb.user.entity.vo.Email;
import com.prgrms.amabnb.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @DisplayName("저장된 유저가 없을 경우 유저를 저장하고 유저 정보를 반환한다.")
    @Test
    void register_NotSavedUser() {
        // given
        UserProfile userProfile = createUserProfile();
        given(userRepository.findByOauthId(any(String.class))).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willReturn(createUser(1L, UserRole.GUEST));

        // when
        UserRegisterResponse registerResponse = userService.register(userProfile);

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
        UserProfile userProfile = createUserProfile();
        given(userRepository.findByOauthId(any(String.class))).willReturn(Optional.of(createUser(2L, UserRole.HOST)));

        // when
        UserRegisterResponse registerResponse = userService.register(userProfile);

        // then
        assertAll(
            () -> assertThat(registerResponse.id()).isEqualTo(2L),
            () -> assertThat(registerResponse.role()).isEqualTo("ROLE_HOST")
        );
    }

    private UserProfile createUserProfile() {
        return UserProfile.builder()
            .oauthId("1")
            .provider("kakao")
            .name("아만드")
            .email("asdasd@gmail.com")
            .profileImgUrl("url")
            .build();
    }

    private User createUser(long userId, UserRole userRole) {
        return User.builder()
            .id(userId)
            .oauthId("1")
            .provider("kakao")
            .name("아만드")
            .email(new Email("aramnd@gmail.com"))
            .userRole(userRole)
            .profileImgUrl("url")
            .build();
    }

}
