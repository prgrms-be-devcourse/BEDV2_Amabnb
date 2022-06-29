package com.prgrms.amabnb.security.util;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.HttpHeaders.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class AuthorizationExtractorTest {

    @DisplayName("헤더에서 토큰을 추출한다.")
    @Test
    void extract() {
        // given
        String headerToken = "token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, "Bearer" + headerToken);

        // when
        String token = AuthorizationExtractor.extract(request);

        // then
        assertThat(token).isEqualTo(headerToken);
    }

    @DisplayName("헤더에 값이 없다면 null을 반환한다.")
    @Test
    void extract_Null() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        String token = AuthorizationExtractor.extract(request);

        // then
        assertThat(token).isNull();
    }

}
