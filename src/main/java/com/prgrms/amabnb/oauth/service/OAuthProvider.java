package com.prgrms.amabnb.oauth.service;

import java.time.LocalDate;
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
        public User toUser(OAuth2User oauth) {
            Map<String, Object> response = oauth.getAttributes();
            Map<String, Object> properties = oauth.getAttribute("properties");
            Map<String, Object> account = oauth.getAttribute("kakao_account");

            return User.builder()
                .oauthId("" + response.get("id"))
                .provider(String.valueOf(OAuthProvider.KAKAO))
                .name("" + properties.get("nickname"))
                .birth(LocalDate.now())
                .email(new Email("" + account.get("email")))
                .profileImgUrl("" + properties.get("profile_image"))
                .userRole(UserRole.GUEST)
                .build();
        }
    };

    private final String name;

    public abstract User toUser(OAuth2User userInfo);
}
