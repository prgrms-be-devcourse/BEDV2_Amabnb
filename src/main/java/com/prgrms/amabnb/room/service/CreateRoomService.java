package com.prgrms.amabnb.room.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.common.exception.EntityNotFoundException;
import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.repository.RoomRepository;
import com.prgrms.amabnb.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateRoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public Long createRoom(CreateRoomRequest createRoomRequest) {
        Room room = createRoomRequest.toRoom();
        room.addRoomImages(createRoomRequest.toRoomImages());
        room.setUser(userRepository.findById(createRoomRequest.getUserId())
            .orElseThrow(() -> new EntityNotFoundException("유저를 찾지 못했습니다")));
        return roomRepository.save(room).getId();
    }

}
