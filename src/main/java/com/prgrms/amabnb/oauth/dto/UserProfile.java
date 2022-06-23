package com.prgrms.amabnb.oauth.dto;

import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.entity.UserRole;
import com.prgrms.amabnb.user.entity.vo.Email;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserProfile {
    private final String oauthId;
    private final String provider;
    private final String name;
    private final String email;
    private final String profileImgUrl;

    @Builder
    public UserProfile(String oauthId, String provider, String name, String email, String profileImgUrl) {
        this.oauthId = oauthId;
        this.provider = provider;
        this.name = name;
        this.email = email;
        this.profileImgUrl = profileImgUrl;
    }

    public User toUser() {
        return User.builder()
            .oauthId(oauthId)
            .provider(provider)
            .name(name)
            .email(new Email(email))
            .profileImgUrl(profileImgUrl)
            .userRole(UserRole.GUEST)
            .build();
    }

}
