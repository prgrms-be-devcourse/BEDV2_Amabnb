package com.prgrms.amabnb.user.entity.vo;

import static org.assertj.core.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class EmailTest {

    private static Stream<Arguments> provideNormalEmail() {
        return Stream.of(
            Arguments.of("spancer@gmail.com"),
            Arguments.of("spancer@google.co.kr" + "c".repeat(80))
        );

    }

    @DisplayName("알맞은 이메일값이라면 이메일 객체를 생성한다.")
    @ParameterizedTest
    @MethodSource("provideNormalEmail")
    void generate_email(String value) {
        // given
        // when
        var email = new Email(value);

        // then
        assertThat(email).isNotNull();
    }

    @DisplayName("이메일값이 null이거나 비어있으면 안된다.")
    @ParameterizedTest
    @NullAndEmptySource
    void generate_email_null_and_empty(String value) {
        assertThatThrownBy(() -> new Email(value))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("잘못된 이메일 포맷이면 안된다.")
    @ParameterizedTest
    @ValueSource(strings = {"spancergaml.com", "spancer@gmailcom", "spancer!gmail.com", "@gmail.com"})
    void generate_email_wrong_email_format(String value) {
        assertThatThrownBy(() -> new Email(value))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("이메일값이 100자를 초과하면 안된다.")
    @Test
    void generate_email_over_max_length() {
        // given
        var value = "spancer@google.co.kr" + "c".repeat(91);

        assertThatThrownBy(() -> new Email(value))
            .isInstanceOf(IllegalArgumentException.class);
    }

}