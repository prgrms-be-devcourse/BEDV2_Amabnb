package com.prgrms.amabnb.user.entity.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PhoneNumberTest {

    @ParameterizedTest
    @DisplayName("알맞은_형식으로_전화번호_VO를_생성한다.")
    @ValueSource(strings = {"010-1234-5678", "010-123-4567"})
    void phoneValidate_success(String number) {
        // given
        // when
        var phoneNum = new PhoneNumber(number);

        // then
        assertThat(phoneNum.getNumber()).isEqualTo(number);
    }

    @Nested
    @DisplayName("알맞은_전화번호_형식이_아니면_예외가_발생한다.")
    class FailValidation {
        @ParameterizedTest
        @DisplayName("핸드폰번호는_공백이면_안된다")
        @NullAndEmptySource
        void emptyPhoneNumber(String number) {
            assertThatThrownBy(() -> new PhoneNumber(number))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @DisplayName("핸드폰번호는_제대로_된_하이픈_사용을_요구한다.")
        @ValueSource(strings = {"010--1234-5678", "010/1234-5678", "01012345678"})
        void invalidHyphen(String number) {
            assertThatThrownBy(() -> new PhoneNumber(number))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @DisplayName("핸드폰번호는_정확한_핸드폰_길이를_요구한다.")
        @ValueSource(strings = {"0100-1234-5678", "010-12345-6789", "010-1234-56789", "01-1234-5678"})
        void invalidLength(String number) {
            assertThatThrownBy(() -> new PhoneNumber(number))
                .isInstanceOf(IllegalArgumentException.class);
        }

    }
}