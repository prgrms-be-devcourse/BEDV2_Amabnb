package com.prgrms.amabnb.token.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String refreshToken;

    @Column(nullable = false, unique = true)
    private long userId;

    public Token(String refreshToken, Long userId) {
        this(null, refreshToken, userId);
    }

    public Token(Long id, String refreshToken, Long userId) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.userId = userId;
    }

}
