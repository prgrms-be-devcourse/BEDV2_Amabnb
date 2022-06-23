package com.prgrms.amabnb.oauth.dto;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshTokenRequest {

    @NotNull(message = "refreshToken은 비어있을 수 없습니다.")
    private String refreshToken;

}
