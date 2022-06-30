package com.prgrms.amabnb.reservation.dto.response;

import com.prgrms.amabnb.user.entity.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HostInfoResponse {

    private Long hostId;
    private String name;
    private String email;

    public HostInfoResponse(Long hostId, String name, String email) {
        this.hostId = hostId;
        this.name = name;
        this.email = email;
    }

    public static HostInfoResponse from(User host) {
        return new HostInfoResponse(
            host.getId(),
            host.getName(),
            host.getEmail().getValue()
        );
    }

}
