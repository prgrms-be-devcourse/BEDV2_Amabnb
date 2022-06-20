package com.prgrms.amabnb.user.entity;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.prgrms.amabnb.common.model.BaseEntity;
import com.prgrms.amabnb.user.entity.vo.Email;
import com.prgrms.amabnb.user.entity.vo.PhoneNumber;

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
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String oauthId;

    @Column(nullable = false)
    private String provider;

    @Column(length = NAME_MAX_LENGTH, nullable = false)
    private String name;

    private LocalDate birth;

    @Embedded
    private Email email;

    @Embedded
    private PhoneNumber phoneNumber;

    @Column(nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Builder
    public User(Long id, String oauthId, String provider, String name, LocalDate birth,
        Email email, PhoneNumber phoneNumber, String imageUrl, UserRole userRole) {
        setOauthId(oauthId);
        setName(name);
        setProvider(provider);
        setImageUrl(imageUrl);
        setBirth(birth);
        this.id = id;
        this.birth = birth;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userRole = userRole;
    }

    private void setImageUrl(String imageUrl) {
        if (Objects.isNull(imageUrl) || imageUrl.isBlank()) {
            throw new IllegalArgumentException();
        }
        this.imageUrl = imageUrl;
    }

    private void setProvider(String provider) {
        if (Objects.isNull(provider) || provider.isBlank()) {
            throw new IllegalArgumentException();
        }
        this.provider = provider;
    }

    private void setName(String name) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new IllegalArgumentException();
        }

        if (name.length() > NAME_MAX_LENGTH) {
            throw new IllegalArgumentException();
        }
        this.name = name;
    }

    private void setOauthId(String oauthId) {
        if (Objects.isNull(oauthId) || oauthId.isBlank()) {
            throw new IllegalArgumentException();
        }
        this.oauthId = oauthId;
    }

    public Optional<LocalDate> getBirth() {
        return Optional.ofNullable(birth);
    }

    private void setBirth(LocalDate birth) {
        if (Objects.nonNull(birth)) {
            validateBirth(birth);
        }
        this.birth = birth;
    }

    private void validateBirth(LocalDate birth) {
        if (birth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException();
        }
    }

    public Optional<PhoneNumber> getPhoneNumber() {
        return Optional.ofNullable(this.phoneNumber);
    }
}
