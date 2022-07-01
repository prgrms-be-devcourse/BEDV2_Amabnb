package com.prgrms.amabnb.room.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.dto.request.ModifyRoomRequest;
import com.prgrms.amabnb.room.dto.response.RoomResponse;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import com.prgrms.amabnb.room.exception.RoomNotFoundException;
import com.prgrms.amabnb.room.repository.RoomRepository;
import com.prgrms.amabnb.user.exception.UserNotFoundException;
import com.prgrms.amabnb.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HostRoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createRoom(Long id, CreateRoomRequest createRoomRequest) {
        Room room = createRoomRequest.toRoom();
        room.addRoomImages(createRoomRequest.toRoomImages());
        room.setHost(userRepository.findById(id).orElseThrow(UserNotFoundException::new));
        return roomRepository.save(room).getId();
    }

    @Transactional
    public void modifyRoom(Long roomId, ModifyRoomRequest modifyRoomRequest) {
        Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);
        changeRoomData(modifyRoomRequest, room);
    }

    public List<RoomResponse> searchRoomsForHost(Long hostId) {
        isExistUser(hostId);

        return roomRepository.findRoomsByUserIdForHost(hostId)
            .stream()
            .map(RoomResponse::from)
            .toList();
    }

    private void changeRoomData(ModifyRoomRequest modifyRoomRequest, Room room) {
        room.changeName(modifyRoomRequest.getName());
        room.changePrice(new Money(modifyRoomRequest.getPrice()));
        room.changeDescription(modifyRoomRequest.getDescription());
        room.changeMaxGuestNum(modifyRoomRequest.getMaxGuestNum());
        room.changeRoomOption(new RoomOption(
            modifyRoomRequest.getBedCnt(), modifyRoomRequest.getBedRoomCnt(), modifyRoomRequest.getBathRoomCnt()
        ));
    }

    private void isExistUser(Long hostId) {
        if (!userRepository.existsById(hostId)) {
            throw new UserNotFoundException();
        }
    }
}
