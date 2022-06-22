package com.prgrms.amabnb.room.service;

import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.dto.request.CreateRoomRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateRoomServiceTest {

    @InjectMocks
    CreateRoomService createRoomService;

    @Test
    @DisplayName("숙소를 생성할 수 있다.")
    void createRoomTest() {
        //given
        CreateRoomRequest createRoomRequest = CreateRoomRequest.builder()
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

        //then
        createRoomService.createRoom(createRoomRequest);
    }
}