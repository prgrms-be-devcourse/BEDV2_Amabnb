package com.prgrms.amabnb.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.auth.entity.Token;
import com.prgrms.amabnb.auth.entity.TokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {

    private final TokenRepository tokenRepository;

    @Transactional
    public void saveRefreshToken(String refreshToken, long userId) {
        tokenRepository.save(new Token(refreshToken, userId));
    }

    public boolean validRefreshToken(String refreshToken) {
        return tokenRepository.existsByRefreshToken(refreshToken);
    }
    
}
