package com.prgrms.amabnb.oauth.entity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
    
    Optional<Token> findTokenByUserId(long userId);

}
