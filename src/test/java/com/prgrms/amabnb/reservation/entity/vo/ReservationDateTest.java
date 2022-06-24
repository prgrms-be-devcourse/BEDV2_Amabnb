package com.prgrms.amabnb.reservation.entity.vo;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.prgrms.amabnb.reservation.exception.ReservationInvalidValueException;

class ReservationDateTest {

    private static Stream<Arguments> provideDate() {
        return Stream.of(
            Arguments.of(null, LocalDate.now()),
            Arguments.of(LocalDate.now(), null)
        );
    }

    @DisplayName("예약 날짜 객체를 생성한다.")
    @Test
    void create() {
        // given
        LocalDate checkIn = LocalDate.of(2022, 6, 25);
        LocalDate checkOut = LocalDate.of(2022, 6, 27);

        // when
        ReservationDate reservationDate = new ReservationDate(checkIn, checkOut);

        // then
        assertThat(reservationDate).isNotNull();
    }

    @DisplayName("예약 날짜는 비어있을 수 없다.")
    @ParameterizedTest
    @MethodSource("provideDate")
    void create_null_checkIn(LocalDate checkIn, LocalDate checkOut) {
        assertThatThrownBy(() -> new ReservationDate(checkIn, checkOut))
            .isInstanceOf(ReservationInvalidValueException.class)
            .hasMessage("예약 날짜는 비어있을 수 없습니다.");
    }

    @DisplayName("체크인이 체크아웃보다 늦을 수 없다.")
    @Test
    void create_CheckIn_After_Checkout() {
        // given
        LocalDate checkIn = LocalDate.of(2022, 6, 30);
        LocalDate checkOut = LocalDate.of(2022, 6, 20);

        // when
        // then
        assertThatThrownBy(() -> new ReservationDate(checkIn, checkOut))
            .isInstanceOf(ReservationInvalidValueException.class)
            .hasMessage("체크 아웃 날짜가 체크인 날짜 전일 수 없습니다.");
    }

}
