package com.prgrms.amabnb.oauth.service;

import java.util.Arrays;
import java.util.Map;

import org.springframework.security.oauth2.core.user.OAuth2User;

import com.prgrms.amabnb.oauth.dto.UserProfile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuthProvider {
    KAKAO("kakao") {
        @Override
        public UserProfile toUserProfile(OAuth2User oauth) {
            Map<String, Object> response = oauth.getAttributes();
            Map<String, Object> properties = oauth.getAttribute("properties");
            Map<String, Object> account = oauth.getAttribute("kakao_account");

            return UserProfile.builder()
                .oauthId(String.valueOf(response.get("id")))
                .provider(KAKAO.name)
                .name(String.valueOf(properties.get("nickname")))
                .email(String.valueOf(account.get("email")))
                .profileImgUrl(String.valueOf(properties.get("profile_image")))
                .build();
        }
    };

    private final String name;

    public static OAuthProvider getProviderFromName(String providerName) {
        return Arrays.stream(values())
            .filter(provider -> provider.name.equals(providerName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 로그인입니다."));
    }

    public abstract UserProfile toUserProfile(OAuth2User userInfo);

}
