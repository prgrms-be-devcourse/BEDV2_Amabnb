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
import com.prgrms.amabnb.common.model.Money;
import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import com.prgrms.amabnb.room.repository.RoomRepository;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.entity.UserRole;
import com.prgrms.amabnb.user.entity.vo.Email;
import com.prgrms.amabnb.user.entity.vo.PhoneNumber;
import com.prgrms.amabnb.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CreateRoomServiceTest {

    @InjectMocks
    CreateRoomService createRoomService;

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
        Long savedRoomId = createRoomService.createRoom(createRoomRequest);

        //then
        then(roomRepository).should(times(1)).save(any(Room.class));
        then(userRepository).should(times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("유저를 찾지 못하면 숙소를 등록할 수 없다.")
    void name() {
        //given
        CreateRoomRequest createRoomRequest = createCreateRoomRequest();
        User user = createUser();
        given(userRepository.findById(anyLong())).willThrow(EntityNotFoundException.class);
        //when,then
        assertThatThrownBy(() -> createRoomService.createRoom(createRoomRequest))
            .isInstanceOf(EntityNotFoundException.class);
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
            .userId(2L)
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
