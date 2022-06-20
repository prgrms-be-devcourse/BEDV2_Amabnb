package com.prgrms.amabnb.room.entity;

import com.prgrms.amabnb.common.model.Money;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    private final RoomAddress roomAddress = new RoomAddress("00000", "창원", "의창구");
    private final Money price = new Money(20000);

    @Test
    @DisplayName("Room을 생성할 수 있다")
    void createRoom() {
        //given
        Room.builder()
                .id(1l)
                .maxGuestNum(1)
                .description("방 설명 입니다")
                .address(roomAddress)
                .price(price)
                .roomType(RoomType.APARTMENT)
                .roomScope(RoomScope.PRIVATE)
                .build();
    }

    @Test
    @DisplayName("id를 제외한 모든 값은 있어야한다")
    void nullableId() {
        //then
        Room.builder()
                .maxGuestNum(1)
                .description("방 설명 입니다")
                .address(roomAddress)
                .price(price)
                .roomType(RoomType.APARTMENT)
                .roomScope(RoomScope.PRIVATE)
                .build();
    }

    @ParameterizedTest
    @DisplayName("게스트 수는 0이하는 될 수 없다.")
    @ValueSource(
            ints = {0, -1, -123}
    )
    void notMinusAndZeroGuestNum(int guestNum) {
        //then
        assertThrows(IllegalArgumentException.class,
                () -> Room.builder()
                        .id(1l)
                        .maxGuestNum(guestNum)
                        .description("방 설명 입니다")
                        .address(roomAddress)
                        .price(price)
                        .roomType(RoomType.APARTMENT)
                        .roomScope(RoomScope.PRIVATE)
                        .build()
        );
    }

    @DisplayName("방 설명은 빈값을 가질 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void notBlankDescription(String description) {
        //then
        assertThrows(IllegalArgumentException.class,
                () -> Room.builder()
                        .id(1l)
                        .maxGuestNum(1)
                        .description(description)
                        .address(roomAddress)
                        .price(price)
                        .roomType(RoomType.APARTMENT)
                        .roomScope(RoomScope.PRIVATE)
                        .build()
        );
    }
}