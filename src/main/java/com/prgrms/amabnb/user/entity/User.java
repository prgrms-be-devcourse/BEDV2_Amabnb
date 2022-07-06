package com.prgrms.amabnb.user.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.prgrms.amabnb.common.model.BaseEntity;
import com.prgrms.amabnb.common.vo.Email;
import com.prgrms.amabnb.user.exception.UserInvalidValueException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    private static final int NAME_MAX_LENGTH = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String oauthId;

    @Column(nullable = false)
    private String provider;

    @Column(length = NAME_MAX_LENGTH, nullable = false)
    private String name;

    @Embedded
    private Email email;

    @Column(nullable = false)
    private String profileImgUrl;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Builder
    public User(Long id, String oauthId, String provider, String name, Email email,
        String profileImgUrl, UserRole userRole) {
        setOauthId(oauthId);
        setName(name);
        setProvider(provider);
        setProfileImgUrl(profileImgUrl);
        this.id = id;
        this.email = email;
        this.userRole = userRole;
    }

    public boolean isSame(User user) {
        return this.id.equals(user.id);
    }

    private void setProfileImgUrl(String imageUrl) {
        if (Objects.isNull(imageUrl) || imageUrl.isBlank()) {
            throw new UserInvalidValueException("이미지 URL은 비어있을 수 없습니다.");
        }
        this.profileImgUrl = imageUrl;
    }

    private void setProvider(String provider) {
        if (Objects.isNull(provider) || provider.isBlank()) {
            throw new UserInvalidValueException("제공자는 비어있을 수 없습니다.");
        }
        this.provider = provider;
    }

    private void setName(String name) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new UserInvalidValueException("이름은 비어있을 수 없습니다.");
        }

        if (name.length() > NAME_MAX_LENGTH) {
            throw new UserInvalidValueException(
                "이름은 %d자 이하여야 합니다. 현재 이름 길이: %d".formatted(NAME_MAX_LENGTH, name.length())
            );
        }
        this.name = name;
    }

    private void setOauthId(String oauthId) {
        if (Objects.isNull(oauthId) || oauthId.isBlank()) {
            throw new UserInvalidValueException("인증 아이디는 비어있을 수 없습니다.");
        }
        this.oauthId = oauthId;
    }

}
