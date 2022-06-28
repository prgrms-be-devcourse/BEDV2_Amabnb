package com.prgrms.amabnb.user.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.amabnb.security.jwt.JwtAuthentication;
import com.prgrms.amabnb.user.dto.response.MyUserInfoResponse;
import com.prgrms.amabnb.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserApi {

    private final UserService userService;

    @GetMapping("/me")
    ResponseEntity<MyUserInfoResponse> myPage(@AuthenticationPrincipal JwtAuthentication user) {
        return ResponseEntity.ok().body(userService.findUserInfo(user.id()));
    }

    @DeleteMapping("/me")
    ResponseEntity<String> deleteAccount(@AuthenticationPrincipal JwtAuthentication user) {
        return ResponseEntity.ok().body(userService.deleteUserAccount(user.id()));
    }

}
