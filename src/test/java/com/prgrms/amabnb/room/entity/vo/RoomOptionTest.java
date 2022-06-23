package com.prgrms.amabnb.room.entity.vo;

import com.prgrms.amabnb.room.exception.RoomInvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class RoomOptionTest {

    @ParameterizedTest
    @DisplayName("RoomOption을 생성할 수 있다")
    @CsvSource(
            value = {"0,0,0", "1,1,1", "22,33,44"}
    )
    void createRoomOption(int bedCnt, int bedRoomCnt, int bathRoomCnt) {
        new RoomOption(bedCnt, bedRoomCnt, bathRoomCnt);
    }

    @Test
    @DisplayName("침대 수는 음수가 될 수 없다")
    void notMinusBedCnt() {
        //given
        int bedCnt = -1;
        //then
        assertThrows(RoomInvalidValueException.class, () -> new RoomOption(bedCnt, 1, 1));
    }

    @Test
    @DisplayName("침실 수는 음수가 될 수 없다")
    void notMinusBedRoomCnt() {
        //given
        int bedRoomCnt = -1;
        //then
        assertThrows(RoomInvalidValueException.class, () -> new RoomOption(1, bedRoomCnt, 1));
    }

    @Test
    @DisplayName("욕실 수는 음수가 될 수 없다")
    void notMinusBathRoomCnt() {
        //given
        int bathRoomCnt = -1;
        //then
        assertThrows(RoomInvalidValueException.class, () -> new RoomOption(0, 1, bathRoomCnt));
    }
}