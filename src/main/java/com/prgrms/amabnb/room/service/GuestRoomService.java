package com.prgrms.amabnb.room.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.prgrms.amabnb.room.dto.request.SearchRoomFilterCondition;
import com.prgrms.amabnb.room.dto.response.RoomResponse;
import com.prgrms.amabnb.room.dto.response.RoomSearchResponse;
import com.prgrms.amabnb.room.exception.RoomNotFoundException;
import com.prgrms.amabnb.room.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuestRoomService {

    private final RoomRepository roomRepository;

    public List<RoomSearchResponse> searchRoomsByFilterCondition(SearchRoomFilterCondition filterCondition,
        Pageable pageable) {

        return roomRepository.findRoomsByFilterCondition(filterCondition, pageable);

    }

    public RoomResponse searchRoomDetail(Long roomId) {
        return RoomResponse.from(roomRepository.findRoomById(roomId).orElseThrow(RoomNotFoundException::new));
    }

}
