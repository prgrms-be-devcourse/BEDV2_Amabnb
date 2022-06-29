package com.prgrms.amabnb.room.api;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.dto.request.PageRoomRequest;
import com.prgrms.amabnb.room.dto.request.SearchRoomFilterCondition;
import com.prgrms.amabnb.room.dto.response.RoomResponse;
import com.prgrms.amabnb.room.service.CreateRoomService;
import com.prgrms.amabnb.room.service.SearchRoomService;
import com.prgrms.amabnb.security.jwt.JwtAuthentication;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomApiController {

    private final CreateRoomService createRoomService;
    private final SearchRoomService searchRoomService;

    @PostMapping
    public ResponseEntity<Void> createRoom(

        @Valid @RequestBody CreateRoomRequest createRoomRequest,
        @AuthenticationPrincipal JwtAuthentication user
    ) {
        Long savedRoomId = createRoomService.createRoom(user.id(), createRoomRequest);
        return ResponseEntity.created(URI.create("/rooms/" + savedRoomId)).build();
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getRooms(SearchRoomFilterCondition searchRoomFilterCondition,
        PageRoomRequest pageRoomRequest) {

        List<RoomResponse> roomResponses = searchRoomService.searchRoomsByFilterCondition(
            searchRoomFilterCondition, pageRoomRequest.of());

        return ResponseEntity.ok(roomResponses);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponse> getRoomDetail(@PathVariable Long roomId) {
        RoomResponse roomResponse = searchRoomService.searchRoomDetail(roomId);
        return ResponseEntity.ok(roomResponse);
    }

}
