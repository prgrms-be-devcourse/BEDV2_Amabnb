package com.prgrms.amabnb.oauth.service;

import java.util.Map;

import org.springframework.security.oauth2.core.user.OAuth2User;

import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.entity.UserRole;
import com.prgrms.amabnb.user.entity.vo.Email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuthProvider {
    KAKAO("KAKAO") {
        @Override
        public User toUser(OAuth2User oauth) {
            Map<String, Object> response = oauth.getAttributes();
            Map<String, Object> properties = oauth.getAttribute("properties");
            Map<String, Object> account = oauth.getAttribute("kakao_account");

            return User.builder()
                .oauthId(String.valueOf(response.get("id")))
                .provider(String.valueOf(OAuthProvider.KAKAO))
                .name((String)properties.get("nickname"))
                .email(new Email((String)account.get("email")))
                .profileImgUrl((String)properties.get("profile_image"))
                .userRole(UserRole.GUEST)
                .build();
        }
    };

    private final String name;

    public abstract User toUser(OAuth2User userInfo);

}
