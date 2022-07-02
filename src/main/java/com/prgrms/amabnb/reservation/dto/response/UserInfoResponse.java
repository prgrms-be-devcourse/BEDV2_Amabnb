package com.prgrms.amabnb.reservation.dto.response;

import com.prgrms.amabnb.user.entity.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserInfoResponse {

    private Long id;
    private String name;
    private String email;

    public UserInfoResponse(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(
            user.getId(),
            user.getName(),
            user.getEmail().getValue()
        );
    }

}
