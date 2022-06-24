package com.prgrms.amabnb.oauth.service;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.oauth.dto.TokenResponse;
import com.prgrms.amabnb.oauth.dto.UserProfile;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuthService {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Transactional
    public TokenResponse register(String registerId, OAuth2User oauth) {
        UserProfile userProfile = OAuthProvider.findByRegisterId(registerId)
            .toUserProfile(oauth);
        User user = userRepository.findByOauthId(String.valueOf(userProfile.getOauthId()))
            .orElseGet(() -> {
                    var newUser = createUser(registerId, oauth);
                    return userRepository.save(newUser);
                }
            );

        var userId = user.getId();
        var role = user.getUserRole().getGrantedAuthority();
        return tokenService.createToken(userId, role);
    }

    private User createUser(String registerId, OAuth2User oauth) {
        UserProfile userProfile = OAuthProvider.findByRegisterId(registerId)
            .toUserProfile(oauth);
        return userProfile.toUser();
    }

}