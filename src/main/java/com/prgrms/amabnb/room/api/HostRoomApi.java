package com.prgrms.amabnb.room.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.amabnb.room.dto.request.ModifyRoomRequest;
import com.prgrms.amabnb.room.dto.response.RoomResponse;
import com.prgrms.amabnb.room.service.HostRoomService;
import com.prgrms.amabnb.security.jwt.JwtAuthentication;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/host/rooms")
@RequiredArgsConstructor
public class HostRoomApi {

    private final HostRoomService hostRoomService;

    @PutMapping("/{roomId}")
    public ResponseEntity<Void> modifyRoom(
        @PathVariable Long roomId,
        @Valid @RequestBody ModifyRoomRequest modifyRoomRequest
    ) {
        hostRoomService.modifyRoom(roomId, modifyRoomRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getRoomsForHost(
        @AuthenticationPrincipal JwtAuthentication host
    ) {
        List<RoomResponse> roomResponseList = hostRoomService.searchRoomsForHost(host.id());
        return ResponseEntity.ok(roomResponseList);
    }
}
