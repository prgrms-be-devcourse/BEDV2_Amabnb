package com.prgrms.amabnb.room.service;

import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.repository.CreateRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateRoomService {

    private final CreateRoomRepository createRoomRepository;

    public Long createRoom(CreateRoomRequest createRoomRequest) {
        return createRoomRepository.save(createRoomRequest.toRoom()).getId();
    }
}
