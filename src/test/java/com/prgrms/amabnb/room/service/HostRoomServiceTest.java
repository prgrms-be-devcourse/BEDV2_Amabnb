package com.prgrms.amabnb.room.service;

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
import com.prgrms.amabnb.common.vo.Email;
import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.common.vo.PhoneNumber;
import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.dto.request.ModifyRoomRequest;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import com.prgrms.amabnb.room.exception.RoomNotFoundException;
import com.prgrms.amabnb.room.repository.RoomRepository;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.entity.UserRole;
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
        CreateRoomRequest createRoomRequest = createCreateRoomRequest();
        User user = createUser();
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(roomRepository.save(any())).willReturn(createRoom());

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
        CreateRoomRequest createRoomRequest = createCreateRoomRequest();
        User user = createUser();
        given(userRepository.findById(anyLong())).willThrow(EntityNotFoundException.class);
        //when,then
        assertThatThrownBy(() -> hostRoomService.createRoom(1L, createRoomRequest))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("호스트는 자신이 등록한 방을 수정할 수 있다.")
    void modifyRoomTest() {
        //given
        Room room = createRoom();
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
        given(roomRepository.findRoomsByHostId(anyLong())).willReturn(List.of(createRoom()));
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

    private User createUser() {
        return User.builder()
            .oauthId("testOauthId")
            .provider("testProvider")
            .userRole(UserRole.GUEST)
            .name("testUser")
            .email(new Email("asdsadsad@gmail.com"))
            .phoneNumber(new PhoneNumber("010-2312-1231"))
            .profileImgUrl("urlurlrurlrurlurlurl")
            .build();
    }

    private CreateRoomRequest createCreateRoomRequest() {
        return CreateRoomRequest.builder()
            .name("방이름")
            .price(1)
            .description("방설명")
            .maxGuestNum(1)
            .zipcode("00000")
            .address("창원")
            .detailAddress("의창구")
            .bedCnt(2)
            .bedRoomCnt(1)
            .bathRoomCnt(1)
            .roomType(RoomType.APARTMENT)
            .roomScope(RoomScope.PRIVATE)
            .imagePaths(List.of("aaa", "bbb"))
            .build();
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

    private Room createRoom() {
        RoomAddress roomAddress = new RoomAddress("00000", "창원", "의창구");
        Money price = new Money(20000);
        RoomOption roomOption = new RoomOption(1, 1, 1);

        return Room.builder()
            .id(1l)
            .name("방 이름")
            .maxGuestNum(1)
            .description("방 설명 입니다")
            .address(roomAddress)
            .price(price)
            .roomOption(roomOption)
            .roomType(RoomType.APARTMENT)
            .roomScope(RoomScope.PRIVATE)
            .build();
    }

}
