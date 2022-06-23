package com.prgrms.amabnb.user.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import com.prgrms.amabnb.user.entity.vo.Email;
import com.prgrms.amabnb.user.entity.vo.PhoneNumber;
import com.prgrms.amabnb.user.exception.UserInvalidValueException;

class UserTest {

    private User.UserBuilder createUserBuilder() {
        return User.builder()
            .oauthId("testOauthId")
            .provider("testProvider")
            .userRole(UserRole.GUEST)
            .name("testUser")
            .birth(LocalDate.of(2000, 1, 12))
            .email(new Email("asdsadsad@gmail.com"))
            .phoneNumber(new PhoneNumber("010-2312-1231"))
            .imageUrl("urlurlrurlrurlurlurl");
    }

    @Test
    @DisplayName("유저를_생성할_수_있다.")
    void constructor() {
        // given
        // when
        var user = createUserBuilder()
            .build();

        // then
        assertAll(
            () -> assertThat(user.getOauthId()).isEqualTo("testOauthId"),
            () -> assertThat(user.getProvider()).isEqualTo("testProvider"),
            () -> assertThat(user.getName()).isEqualTo("testUser"),
            () -> assertThat(user.getUserRole()).isEqualTo(UserRole.GUEST),
            () -> assertThat(user.getUserRole().getRole()).isEqualTo(UserRole.GUEST.getRole()),
            () -> assertThat(user.getBirth().get()).isEqualTo(LocalDate.of(2000, 1, 12)),
            () -> assertThat(user.getEmail()).isEqualTo(new Email("asdsadsad@gmail.com")),
            () -> assertThat(user.getPhoneNumber().get()).isEqualTo(new PhoneNumber("010-2312-1231")),
            () -> assertThat(user.getImageUrl()).isEqualTo("urlurlrurlrurlurlurl")
        );
    }

    @DisplayName("유저 생성 실패")
    @Nested
    class ValidationFailure {

        @DisplayName("oauthId는 null이거나 비어있으면 안된다.")
        @ParameterizedTest
        @NullAndEmptySource
        void oauthId(String source) {
            assertThatThrownBy(() -> createUserBuilder()
                .oauthId(source)
                .build()
            ).isInstanceOf(UserInvalidValueException.class)
                .hasMessage("인증 아이디는 비어있을 수 없습니다.");
        }

        @DisplayName("provider는 null이거나 비어있으면 안된다.")
        @ParameterizedTest
        @NullAndEmptySource
        void provider(String source) {
            assertThatThrownBy(() -> createUserBuilder()
                .provider(source)
                .build()
            ).isInstanceOf(UserInvalidValueException.class)
                .hasMessage("제공자는 비어있을 수 없습니다.");
        }

        @DisplayName("name는 null이거나 비어있으면 안된다.")
        @ParameterizedTest
        @NullAndEmptySource
        void name(String source) {
            assertThatThrownBy(() -> createUserBuilder()
                .name(source)
                .build()
            ).isInstanceOf(UserInvalidValueException.class)
                .hasMessage("이름은 비어있을 수 없습니다.");
        }

        @DisplayName("name는 20자 초과이면 안된다.")
        @Test
        void name_over_max_length() {
            assertThatThrownBy(() -> createUserBuilder()
                .name("a".repeat(21))
                .build()
            ).isInstanceOf(UserInvalidValueException.class)
                .hasMessage("이름은 20자 이하여야 합니다. 현재 이름 길이: 21");
        }

        @DisplayName("birth는 현재보다 미래면 안된다.")
        @Test
        void birth_past_than_now() {
            assertThatThrownBy(() -> createUserBuilder()
                .birth(LocalDate.MAX)
                .build()
            ).isInstanceOf(UserInvalidValueException.class)
                .hasMessage("생일은 현재보다 미래일 수 없습니다.");
        }

        @DisplayName("imageUrl은 null이거나 비어있으면 안된다.")
        @ParameterizedTest
        @NullAndEmptySource
        void imageUrl(String source) {
            assertThatThrownBy(() -> createUserBuilder()
                .imageUrl(source)
                .build()
            ).isInstanceOf(UserInvalidValueException.class)
                .hasMessage("이미지 URL은 비어있을 수 없습니다.");
        }
    }
}
