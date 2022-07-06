package com.prgrms.amabnb.room.service;

import static com.prgrms.amabnb.config.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.amabnb.common.exception.EntityNotFoundException;
import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.dto.request.ModifyRoomRequest;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.exception.RoomNotFoundException;
import com.prgrms.amabnb.room.repository.RoomRepository;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.exception.UserNotFoundException;
import com.prgrms.amabnb.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class HostRoomServiceTest {

    @InjectMocks
    HostRoomService hostRoomService;

    @Mock
    RoomRepository roomRepository;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("숙소를 생성할 수 있다.")
    void createRoomTest() {
        //given
        CreateRoomRequest createRoomRequest = createRoomRequest();
        User user = createUser("fdas");
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(roomRepository.save(any())).willReturn(createRoom(user));

        //when
        Long savedRoomId = hostRoomService.createRoom(1L, createRoomRequest);

        //then
        then(roomRepository).should(times(1)).save(any(Room.class));
        then(userRepository).should(times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("유저를 찾지 못하면 숙소를 등록할 수 없다.")
    void createFailTest() {
        //given
        CreateRoomRequest createRoomRequest = createRoomRequest();
        User user = createUser("fdsa");
        given(userRepository.findById(anyLong())).willThrow(EntityNotFoundException.class);
        //when,then
        assertThatThrownBy(() -> hostRoomService.createRoom(1L, createRoomRequest))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("호스트는 자신이 등록한 방을 수정할 수 있다.")
    void modifyRoomTest() {
        //given
        Room room = createRoom(createUser("fdas"));
        given(roomRepository.findRoomByIdAndHostId(anyLong(), anyLong())).willReturn(Optional.of(room));
        //when
        hostRoomService.modifyRoom(1L, 1L, createModifyRoomRequest());
        //then
        then(roomRepository).should(times(1)).findRoomByIdAndHostId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("등록된 방이 아니면 수정할 수 없다.")
    void modifyFailTest() {
        //given
        given(roomRepository.findRoomByIdAndHostId(anyLong(), anyLong())).willThrow(RoomNotFoundException.class);
        //when, then
        assertThatThrownBy(() -> hostRoomService.modifyRoom(anyLong(), anyLong(), createModifyRoomRequest()))
            .isInstanceOf(RoomNotFoundException.class);
    }

    @Test
    @DisplayName("호스트가 등록한 방정보들을 볼 수 있다.")
    void searchRoomsForHostTest() {
        //given
        given(userRepository.existsById(anyLong())).willReturn(true);
        given(roomRepository.findRoomsByHostId(anyLong())).willReturn(List.of(createRoom(createUser("fda"))));
        //when
        hostRoomService.searchRoomsForHost(1L);
        //then
        then(userRepository).should(times(1)).existsById(anyLong());
        then(roomRepository).should(times(1)).findRoomsByHostId(anyLong());
    }

    @Test
    @DisplayName("유저가 존재하지 않으면 방정보를 가져올 수 없다.")
    void name() {
        //given
        given(userRepository.existsById(anyLong())).willReturn(false);
        //when
        assertThatThrownBy(() -> hostRoomService.searchRoomsForHost(1L)).isInstanceOf(UserNotFoundException.class);

    }

    private ModifyRoomRequest createModifyRoomRequest() {
        return ModifyRoomRequest.builder()
            .name("수정된 방이름")
            .price(3333)
            .description("수정된 방설명")
            .maxGuestNum(5)
            .bedCnt(22)
            .bedRoomCnt(11)
            .bathRoomCnt(11)
            .build();
    }

}
