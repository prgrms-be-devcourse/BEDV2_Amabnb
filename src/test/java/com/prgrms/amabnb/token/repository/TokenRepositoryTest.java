package com.prgrms.amabnb.token.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.prgrms.amabnb.config.RepositoryTest;
import com.prgrms.amabnb.token.entity.Token;

class TokenRepositoryTest extends RepositoryTest {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TestEntityManager entityManager;

    @DisplayName("userId로 저장된 token을 찾는다.")
    @Test
    void findTokenByUserId() {
        // given
        String refreshToken = "refreshToken";
        long userId = 1L;
        Token token = new Token(refreshToken, userId);
        tokenRepository.save(token);
        entityManager.flush();
        entityManager.clear();

        // when
        Token findToken = tokenRepository.findTokenByUserId(userId).get();

        // then
        assertThat(findToken.getRefreshToken()).isEqualTo(refreshToken);
    }

}
