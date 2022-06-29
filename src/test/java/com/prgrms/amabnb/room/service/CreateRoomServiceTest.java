package com.prgrms.amabnb.room.service;

import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import com.prgrms.amabnb.room.repository.CreateRoomRepository;

@ExtendWith(MockitoExtension.class)
class CreateRoomServiceTest {

    @InjectMocks
    CreateRoomService createRoomService;

    @Mock
    CreateRoomRepository createRoomRepository;

    @Test
    @DisplayName("숙소를 생성할 수 있다.")
    void createRoomTest() {
        //given
        CreateRoomRequest createRoomRequest = createCreateRoomRequest();
        given(createRoomRepository.save(any())).willReturn(createRoom());

        //when
        Long savedRoomId = createRoomService.createRoom(createRoomRequest);

        //then
        then(createRoomRepository).should(times(1)).save(any(Room.class));
    }

    private CreateRoomRequest createCreateRoomRequest() {
        return CreateRoomRequest.builder()
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
            .build();
    }

    private Room createRoom() {
        RoomAddress roomAddress = new RoomAddress("00000", "창원", "의창구");
        Money price = new Money(20000);
        RoomOption roomOption = new RoomOption(1, 1, 1);

        return Room.builder()
            .id(1l)
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
