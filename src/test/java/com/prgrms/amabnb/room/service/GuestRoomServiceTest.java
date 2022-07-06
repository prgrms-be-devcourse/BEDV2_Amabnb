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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.prgrms.amabnb.room.dto.request.SearchRoomFilterCondition;
import com.prgrms.amabnb.room.dto.response.RoomSearchResponse;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
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
        // //given
        List<RoomSearchResponse> response = List.of(new RoomSearchResponse());
        SearchRoomFilterCondition searchRoomFilterCondition = createSearchRoomFilterCondition();
        given(roomRepository.findRoomsByFilterCondition(any(SearchRoomFilterCondition.class), any(Pageable.class)))
            .willReturn(response);

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
        given(roomRepository.findRoomById(anyLong())).willReturn(Optional.of(createRoom(createUser("fdasf"))));

        //when
        guestRoomService.searchRoomDetail(1L);

        //then
        then(roomRepository).should(times(1)).findRoomById(anyLong());
    }

    @Test
    @DisplayName("등록된 숙소가 아니면 조회할 수 없다.")
    void notSavedRoomDetail() {
        //given
        given(roomRepository.findRoomById(anyLong())).willThrow(new RoomNotFoundException());

        //when
        assertThatThrownBy(() -> guestRoomService.searchRoomDetail(1L))
            .isInstanceOf(RoomNotFoundException.class);

        //then

    }

    private SearchRoomFilterCondition createSearchRoomFilterCondition() {
        return SearchRoomFilterCondition.builder()
            .minBeds(1)
            .minBedrooms(1)
            .minBathrooms(1)
            .minPrice(2000)
            .maxPrice(5000)
            .roomTypes(List.of(RoomType.APARTMENT))
            .roomScopes(List.of(RoomScope.PRIVATE))
            .build();
    }

}
