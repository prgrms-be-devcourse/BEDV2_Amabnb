package com.prgrms.amabnb.security.jwt;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.prgrms.amabnb.common.security.jwt.exception.InvalidTokenException;

class JwtAuthenticationTest {

    @DisplayName("JwtAuthentication를 생성한다.")
    @Test
    void create_JwtAuthentication() {
        // given
        String token = "token";
        Long id = 1L;

        // when
        JwtAuthentication jwtAuthentication = new JwtAuthentication(token, id);

        // then
        assertThat(jwtAuthentication).isNotNull();
    }

    @DisplayName("토큰값이 null이거나 비어있으면 안된다.")
    @ParameterizedTest
    @NullAndEmptySource
    void create_JwtAuthentication_Token_Empty(String token) {
        assertThatThrownBy(() -> new JwtAuthentication(token, 1L))
            .isInstanceOf(InvalidTokenException.class);
    }

    @DisplayName("id값이 0이하면 안된다.")
    @ParameterizedTest
    @ValueSource(longs = {0, -1, -10})
    void create_JwtAuthentication_Id_Under_Zero(long value) {
        assertThatThrownBy(() -> new JwtAuthentication("token", value))
            .isInstanceOf(InvalidTokenException.class);
    }

    @DisplayName("id가 null이면 안된다.")
    @Test
    void create_JwtAuthentication_Id_Null() {
        assertThatThrownBy(() -> new JwtAuthentication("token", null))
            .isInstanceOf(InvalidTokenException.class);
    }

}
