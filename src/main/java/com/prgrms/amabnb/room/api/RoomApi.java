package com.prgrms.amabnb.room.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.amabnb.room.dto.request.PageRoomRequest;
import com.prgrms.amabnb.room.dto.request.SearchRoomFilterCondition;
import com.prgrms.amabnb.room.dto.response.RoomResponse;
import com.prgrms.amabnb.room.dto.response.RoomSearchResponse;
import com.prgrms.amabnb.room.service.GuestRoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomApi {

    private final GuestRoomService guestRoomService;

    @GetMapping
    public ResponseEntity<List<RoomSearchResponse>> getRooms(
        SearchRoomFilterCondition searchRoomFilterCondition,
        PageRoomRequest pageRoomRequest
    ) {

        List<RoomSearchResponse> roomResponses = guestRoomService.searchRoomsByFilterCondition(
            searchRoomFilterCondition, pageRoomRequest.of());

        return ResponseEntity.ok(roomResponses);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponse> getRoomDetail(@PathVariable Long roomId) {
        RoomResponse roomResponse = guestRoomService.searchRoomDetail(roomId);
        return ResponseEntity.ok(roomResponse);
    }

}
