package com.prgrms.amabnb.room.api;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.dto.request.PageRequestDto;
import com.prgrms.amabnb.room.dto.request.SearchRoomFilterCondition;
import com.prgrms.amabnb.room.dto.response.RoomResponse;
import com.prgrms.amabnb.room.service.CreateRoomService;
import com.prgrms.amabnb.room.service.SearchRoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomApiController {

    private final CreateRoomService createRoomService;
    private final SearchRoomService searchRoomService;

    @PostMapping
    public ResponseEntity<Void> createRoom(@Valid @RequestBody CreateRoomRequest createRoomRequest) {
        Long savedRoomId = createRoomService.createRoom(createRoomRequest);
        return ResponseEntity.created(URI.create("/rooms/" + savedRoomId)).build();
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getRooms(
        @Nullable @RequestParam String minBeds,
        @Nullable @RequestParam String minBedrooms,
        @Nullable @RequestParam String minBathrooms,
        @Nullable @RequestParam String minPrice,
        @Nullable @RequestParam String maxPrice,
        @Nullable @RequestParam List<String> roomTypes,
        @Nullable @RequestParam List<String> roomScopes,
        PageRequestDto pageRequestDto) {

        List<RoomResponse> roomResponses = searchRoomService.searchRoomsByFilterCondition(
            toParam(minBeds, minBedrooms, minBathrooms, minPrice, maxPrice, roomTypes, roomScopes),
            PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize()));

        return ResponseEntity.ok(roomResponses);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponse> getRoomDetail(@PathVariable Long roomId) {
        RoomResponse roomResponse = searchRoomService.searchRoomDetail(roomId);
        return ResponseEntity.ok(roomResponse);
    }

    private SearchRoomFilterCondition toParam(String minBeds, String minBedrooms, String minBathrooms, String minPrice,
        String maxPrice, List<String> roomTypes, List<String> roomScopes) {

        return SearchRoomFilterCondition.builder()
            .minBeds(Integer.valueOf(minBeds))
            .minBedrooms(Integer.valueOf(minBedrooms))
            .minBathrooms(Integer.valueOf(minBathrooms))
            .minPrice(Integer.valueOf(minPrice))
            .maxPrice(Integer.valueOf(maxPrice))
            .roomTypes(roomTypes)
            .roomScopes(roomScopes)
            .build();
    }
}
