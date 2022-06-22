package com.prgrms.amabnb.room.api;

import com.prgrms.amabnb.room.entity.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.service.CreateRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomApiController {

    private final CreateRoomService createRoomService;

    @PostMapping
    public ResponseEntity createRoom(@Valid @RequestBody CreateRoomRequest createRoomRequest) {
        createRoomService.createRoom(createRoomRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
