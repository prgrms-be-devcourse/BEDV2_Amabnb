package com.prgrms.amabnb.auth.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {

    boolean existsByRefreshToken(String refreshToken);

}
