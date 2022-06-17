package com.prgrms.amabnb.user.entity;

import java.time.LocalDate;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.prgrms.amabnb.common.model.BaseEntity;
import com.prgrms.amabnb.user.entity.vo.Email;
import com.prgrms.amabnb.user.entity.vo.PhoneNumber;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private LocalDate birthDate;

    @Embedded
    private Email email;

    @Embedded
    private PhoneNumber phoneNumber;

    @Builder
    public User(Long id, String name, LocalDate birthDate, Email email, PhoneNumber phoneNumber) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

}
