package com.prgrms.amabnb.user.api;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.amabnb.user.dto.response.MyUserInfoResponse;
import com.prgrms.amabnb.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserApi {

    private final UserService userService;

    @GetMapping("/me")
    ResponseEntity<MyUserInfoResponse> myPage(Principal user) {
        return ResponseEntity.ok().body(userService.findUserInfo(user));
    }

}
