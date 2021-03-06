package com.prgrms.amabnb.room.entity.vo;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.prgrms.amabnb.room.exception.RoomInvalidValueException;

class RoomAddressTest {

    @DisplayName("RoomAddress 생성할 수 있다")
    @Test
    void createRoomAddress() {
        //given, when
        String zipcode = "06213";
        String address = "창원";
        String detailAddress = "의창구";
        //then
        RoomAddress roomAddress = new RoomAddress(zipcode, address, detailAddress);

        assertAll(
            () -> assertThat(roomAddress.getZipcode()).isEqualTo(zipcode),
            () -> assertThat(roomAddress.getAddress()).isEqualTo(address),
            () -> assertThat(roomAddress.getDetailAddress()).isEqualTo(detailAddress)
        );

    }

    @DisplayName("zipcode는 5자리이고 숫자외에는 입력할 수 없다")
    @ParameterizedTest
    @ValueSource(
        strings = {"", "  ", "adsdd", "asdf3", "666666", "4444", "!!!!!"}
    )
    void notNullAndBlankZipcode(String zipcode) {
        //then
        assertThrows(RoomInvalidValueException.class, () -> new RoomAddress(zipcode, "창원", "의창구"));
    }

    @DisplayName("address는 빈 값이면 안된다")
    @ParameterizedTest
    @NullAndEmptySource
    void notNullAndBlankAddress(String address) {
        //then
        assertThrows(RoomInvalidValueException.class, () -> new RoomAddress("55555", address, "321"));
    }
}
