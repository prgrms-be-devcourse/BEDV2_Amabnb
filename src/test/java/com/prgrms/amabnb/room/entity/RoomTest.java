package com.prgrms.amabnb.room.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import com.prgrms.amabnb.room.exception.RoomInvalidValueException;

class RoomTest {

    private final RoomAddress roomAddress = new RoomAddress("00000", "창원", "의창구");
    private final Money price = new Money(20000);
    private final RoomOption roomOption = new RoomOption(1, 1, 1);

    @DisplayName("Room을 생성할 수 있다")
    @Test
    void createRoom() {
        //given
        Room room = Room.builder()
            .id(1l)
            .name("aa")
            .maxGuestNum(1)
            .description("방 설명 입니다")
            .address(roomAddress)
            .price(price)
            .roomOption(roomOption)
            .roomType(RoomType.APARTMENT)
            .roomScope(RoomScope.PRIVATE)
            .roomImages(List.of(createRoomImage()))
            .build();

        //then
        assertAll(
            () -> assertThat(room.getId()).isEqualTo(1L),
            () -> assertThat(room.getMaxGuestNum()).isEqualTo(1),
            () -> assertThat(room.getDescription()).isEqualTo("방 설명 입니다"),
            () -> assertThat(room.getAddress()).isEqualTo(roomAddress),
            () -> assertThat(room.getPrice()).isEqualTo(price),
            () -> assertThat(room.getRoomOption()).isEqualTo(roomOption),
            () -> assertThat(room.getRoomType()).isEqualTo(RoomType.APARTMENT),
            () -> assertThat(room.getRoomScope()).isEqualTo(RoomScope.PRIVATE)
        );
    }

    @DisplayName("게스트 수는 0이하는 될 수 없다.")
    @ParameterizedTest
    @ValueSource(
        ints = {0, -1, -123}
    )
    void notMinusAndZeroGuestNum(int guestNum) {
        //then
        assertThrows(RoomInvalidValueException.class,
            () -> Room.builder()
                .id(1l)
                .maxGuestNum(guestNum)
                .description("방 설명 입니다")
                .address(roomAddress)
                .price(price)
                .roomOption(roomOption)
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
        assertThrows(RoomInvalidValueException.class,
            () -> Room.builder()
                .id(1l)
                .maxGuestNum(1)
                .description(description)
                .address(roomAddress)
                .price(price)
                .roomOption(roomOption)
                .roomType(RoomType.APARTMENT)
                .roomScope(RoomScope.PRIVATE)
                .build()
        );
    }

    @DisplayName("id를 제외한 모든값은 가지고 있어야한다")
    @Test
    void isPresentTest() {
        assertThatThrownBy(() -> Room.builder().id(1l).build()).isInstanceOf(RoomInvalidValueException.class);
    }

    @DisplayName("허용할 수 있는 인원을 초과하는 지 확인한다.")
    @Test
    void isOverMaxGuestNum() {
        // given
        Room room = Room.builder()
            .name("별이 빛나는 밤")
            .maxGuestNum(5)
            .description("방 설명 입니다")
            .address(roomAddress)
            .price(price)
            .roomOption(roomOption)
            .roomType(RoomType.APARTMENT)
            .roomScope(RoomScope.PRIVATE)
            .roomImages(List.of(createRoomImage()))
            .build();

        // when
        boolean result = room.isOverMaxGuestNum(6);

        // then
        assertThat(result).isTrue();
    }

    private RoomImage createRoomImage() {
        return new RoomImage("aaa");
    }
}
