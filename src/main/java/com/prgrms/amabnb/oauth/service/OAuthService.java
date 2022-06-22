package com.prgrms.amabnb.oauth.service;

import java.util.Map;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.security.jwt.JwtTokenProvider;
import com.prgrms.amabnb.token.Token;
import com.prgrms.amabnb.token.TokenRepository;
import com.prgrms.amabnb.token.TokenResponse;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenResponse register(OAuth2User oauth) {
        Map<String, Object> res = oauth.getAttributes();
        User user = userRepository.findByOauthId(String.valueOf(res.get("id")))
            .orElseGet(() -> {
                    var newUser = OAuthProvider.KAKAO.toUser(oauth);
                    return userRepository.save(newUser);
                }
            );

        long userId = user.getId();
        String role = user.getUserRole().getGrantedAuthority();
        var accessToken = jwtTokenProvider.createAccessToken(userId, role);
        var refreshToken = jwtTokenProvider.createRefreshToken();
        tokenRepository.save(new Token(refreshToken, userId));
        return new TokenResponse(accessToken, refreshToken);
    }

}
