package com.prgrms.amabnb.room.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.prgrms.amabnb.common.exception.EntityNotFoundException;
import com.prgrms.amabnb.room.dto.request.SearchRoomFilterCondition;
import com.prgrms.amabnb.room.dto.response.RoomResponse;
import com.prgrms.amabnb.room.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchRoomService {

    private final RoomRepository roomRepository;

    public List<RoomResponse> searchRoomsByFilterCondition(SearchRoomFilterCondition filterCondition,
        Pageable pageable) {

        List<RoomResponse> roomResponseList = new ArrayList<>();

        roomRepository.findRoomsByFilterCondition(filterCondition, pageable)
            .forEach(room -> roomResponseList.add(RoomResponse.from(room)));

        return roomResponseList;
    }

    public RoomResponse searchRoomDetail(Long roomId) {
        return RoomResponse.from(
            roomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("숙소를 찾지 못했습니다.")));
    }

}
