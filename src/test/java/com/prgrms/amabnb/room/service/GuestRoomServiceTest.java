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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.room.dto.request.SearchRoomFilterCondition;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import com.prgrms.amabnb.room.exception.RoomNotFoundException;
import com.prgrms.amabnb.room.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
class GuestRoomServiceTest {

    @InjectMocks
    GuestRoomService guestRoomService;

    @Mock
    RoomRepository roomRepository;

    @Test
    @DisplayName("필터 검색을 할 수 있다.")
    void searchByFilter() {
        //given
        List<Room> rooms = List.of(createRoom(), createRoom());
        SearchRoomFilterCondition searchRoomFilterCondition = createSearchRoomFilterCondition();
        given(roomRepository.findRoomsByFilterCondition(any(SearchRoomFilterCondition.class), any(Pageable.class)))
            .willReturn(rooms);

        //when
        guestRoomService.searchRoomsByFilterCondition(searchRoomFilterCondition, PageRequest.of(0, 10));

        //then
        then(roomRepository).should(times(1))
            .findRoomsByFilterCondition(any(SearchRoomFilterCondition.class), any(Pageable.class));
    }

    @Test
    @DisplayName("숙소 상세정보를 가져 올 수 있다.")
    void searchRoomDetail() {
        //given
        given(roomRepository.findById(anyLong())).willReturn(Optional.of(createRoom()));

        //when
        guestRoomService.searchRoomDetail(1L);

        //then
        then(roomRepository).should(times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("등록된 숙소가 아니면 조회할 수 없다.")
    void notSavedRoomDetail() {
        //given
        given(roomRepository.findById(anyLong())).willThrow(new RoomNotFoundException());

        //when
        assertThatThrownBy(() -> guestRoomService.searchRoomDetail(1L))
            .isInstanceOf(RoomNotFoundException.class);

        //then

    }

    private Room createRoom() {
        RoomAddress roomAddress = new RoomAddress("00000", "창원", "의창구");
        Money price = new Money(20000);
        RoomOption roomOption = new RoomOption(1, 1, 1);

        return Room.builder()
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

    private SearchRoomFilterCondition createSearchRoomFilterCondition() {
        return new SearchRoomFilterCondition(
            1, 1, 1, 1, 1000000, null, null
        );
    }

}
