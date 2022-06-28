package com.prgrms.amabnb.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.oauth.UserProfile;
import com.prgrms.amabnb.user.dto.response.UserRegisterResponse;
import com.prgrms.amabnb.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserRegisterResponse register(UserProfile userProfile) {
        var user = userRepository.findByOauthId(String.valueOf(userProfile.getOauthId()))
            .orElseGet(() -> userRepository.save(userProfile.toUser()));
        return new UserRegisterResponse(user.getId(), user.getUserRole().getGrantedAuthority());
    }

}
