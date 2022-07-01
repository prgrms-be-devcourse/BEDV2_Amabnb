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
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.dto.request.PageRoomRequest;
import com.prgrms.amabnb.room.dto.request.SearchRoomFilterCondition;
import com.prgrms.amabnb.room.dto.response.RoomResponse;
import com.prgrms.amabnb.room.service.GuestRoomService;
import com.prgrms.amabnb.room.service.HostRoomService;
import com.prgrms.amabnb.security.jwt.JwtAuthentication;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RoomApi {

    private final HostRoomService hostRoomService;
    private final GuestRoomService guestRoomService;

    @PostMapping("/rooms")
    public ResponseEntity<Void> createRoom(
        @Valid @RequestBody CreateRoomRequest createRoomRequest,
        @AuthenticationPrincipal JwtAuthentication user
    ) {
        Long savedRoomId = hostRoomService.createRoom(user.id(), createRoomRequest);
        return ResponseEntity.created(URI.create("/rooms/" + savedRoomId)).build();
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<RoomResponse>> getRooms(SearchRoomFilterCondition searchRoomFilterCondition,
        PageRoomRequest pageRoomRequest) {

        List<RoomResponse> roomResponses = guestRoomService.searchRoomsByFilterCondition(
            searchRoomFilterCondition, pageRoomRequest.of());

        return ResponseEntity.ok(roomResponses);
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<RoomResponse> getRoomDetail(@PathVariable Long roomId) {
        RoomResponse roomResponse = guestRoomService.searchRoomDetail(roomId);
        return ResponseEntity.ok(roomResponse);
    }

}
