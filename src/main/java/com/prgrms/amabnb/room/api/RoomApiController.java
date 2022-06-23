package com.prgrms.amabnb.room.api;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.service.CreateRoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomApiController {

    private final CreateRoomService createRoomService;

    @PostMapping
    public ResponseEntity<Void> createRoom(@Valid @RequestBody CreateRoomRequest createRoomRequest) {
        Long savedRoomId = createRoomService.createRoom(createRoomRequest);
        return ResponseEntity.created(URI.create("/rooms/" + savedRoomId)).build();
    }

}
