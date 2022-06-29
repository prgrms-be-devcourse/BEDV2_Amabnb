package com.prgrms.amabnb.room.api;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.dto.request.SearchRoomFilterCondition;
import com.prgrms.amabnb.room.dto.response.RoomResponse;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
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
    public ResponseEntity<List<RoomResponse>> getRooms(
        @Nullable @RequestParam String minBeds,
        @Nullable @RequestParam String minBedrooms,
        @Nullable @RequestParam String minBathrooms,
        @Nullable @RequestParam String minPrice,
        @Nullable @RequestParam String maxPrice,
        @Nullable @RequestParam List<RoomType> roomTypes,
        @Nullable @RequestParam List<RoomScope> roomScopes,
        Pageable pageable
    ) {

        List<RoomResponse> roomResponses = searchRoomService.searchRoomsByFilterCondition(
            SearchRoomFilterCondition.from(minBeds, minBedrooms, minBathrooms, minPrice, maxPrice, roomTypes,
                roomScopes), pageable);

        return ResponseEntity.ok(roomResponses);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponse> getRoomDetail(@PathVariable Long roomId) {
        RoomResponse roomResponse = searchRoomService.searchRoomDetail(roomId);
        return ResponseEntity.ok(roomResponse);
    }

}
