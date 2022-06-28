package com.prgrms.amabnb.oauth;

import static com.prgrms.amabnb.oauth.OAuthProvider.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OAuthProviderTest {

    @Nested
    class findByRegisterId {
        @Test
        @DisplayName("application에 등록된 provider를 찾는다")
        void kakao() {
            var provider = OAuthProvider.getProviderFromName("kakao");
            assertThat(provider).isEqualTo(KAKAO);
        }

        @Test
        @DisplayName("application에 등록되지 않은 provider는 IllegalArgumentException")
        void cacao() {
            assertThatThrownBy(() -> OAuthProvider.getProviderFromName("cacao"))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
