package com.prgrms.amabnb.oauth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.amabnb.oauth.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findTokenByUserId(long userId);

}
