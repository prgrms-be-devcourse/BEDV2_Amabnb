package com.prgrms.amabnb.reservation.entity.vo;

import static java.time.LocalDate.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.prgrms.amabnb.reservation.exception.ReservationInvalidValueException;
import com.prgrms.amabnb.reservation.exception.ReservationReduceDateException;

class ReservationDateTest {

    private static Stream<Arguments> provideNullDate() {
        return Stream.of(
            Arguments.of(null, now()),
            Arguments.of(now(), null)
        );
    }

    private static Stream<Arguments> provideNowAndBeforeDate() {
        return Stream.of(
            Arguments.of(now()),
            Arguments.of(now().minusDays(1L))
        );
    }

    private static Stream<Arguments> provideInvalidDate() {
        return Stream.of(
            Arguments.of(now().plusDays(3L), now().plusDays(1L)),
            Arguments.of(now().plusDays(3L), now().plusDays(3L))
        );
    }

    @DisplayName("예약 날짜 객체를 생성한다.")
    @Test
    void create() {
        // given
        LocalDate checkIn = now();
        LocalDate checkOut = now().plusDays(1L);

        // when
        ReservationDate reservationDate = new ReservationDate(checkIn, checkOut);

        // then
        assertThat(reservationDate).isNotNull();
    }

    @DisplayName("예약 날짜는 비어있을 수 없다.")
    @ParameterizedTest(name = "checkIn = {0}, checkOut={1}")
    @MethodSource("provideNullDate")
    void create_null_checkIn(LocalDate checkIn, LocalDate checkOut) {
        assertThatThrownBy(() -> new ReservationDate(checkIn, checkOut))
            .isInstanceOf(ReservationInvalidValueException.class)
            .hasMessage("예약 날짜는 비어있을 수 없습니다.");
    }

    @DisplayName("체크아웃이 체크인보다 전이거나 같을 수 없다.")
    @ParameterizedTest
    @MethodSource("provideInvalidDate")
    void create_CheckOut_Before_Or_Same_CheckIn(LocalDate checkIn, LocalDate checkOut) {
        assertThatThrownBy(() -> new ReservationDate(checkIn, checkOut))
            .isInstanceOf(ReservationInvalidValueException.class)
            .hasMessage("체크 아웃 날짜가 체크인 날짜 전이거나 같을 수 없습니다.");
    }

    @DisplayName("체크인이 현재보다 전일 수 없다.")
    @Test
    void create_CheckIn_Before_Now() {
        // given
        LocalDate now = now();

        // when
        // then
        assertThatThrownBy(() -> new ReservationDate(now.minusDays(1L), now.plusDays(3L)))
            .isInstanceOf(ReservationInvalidValueException.class)
            .hasMessage("체크인은 현재보다 전일 수 없습니다.");
    }

    @DisplayName("체크아웃이 현재보다 전이거나 현재일 수 없다.")
    @ParameterizedTest
    @MethodSource("provideNowAndBeforeDate")
    void create_CheckOut_Before_Or_Same_Now(LocalDate checkOut) {
        assertThatThrownBy(() -> new ReservationDate(now(), checkOut))
            .isInstanceOf(ReservationInvalidValueException.class)
            .hasMessage("체크아웃은 현재보다 전이거나 현재일 수 없습니다.");
    }

    @DisplayName("예약 날짜의 기간을 계산한다.")
    @Test
    void getPeriod() {
        // given
        int period = 5;
        LocalDate checkIn = now();
        LocalDate checkOut = checkIn.plusDays(5L);
        ReservationDate reservationDate = new ReservationDate(checkIn, checkOut);

        // when
        int calculatePeriod = reservationDate.getPeriod();

        // then
        assertThat(calculatePeriod).isEqualTo(period);
    }

    @DisplayName("예약 체크아웃을 변경한다.")
    @Test
    void changeCheckOut() {
        // given
        LocalDate checkIn = now();
        LocalDate checkOut = now().plusDays(1L);
        ReservationDate reservationDate = new ReservationDate(checkIn, checkOut);
        LocalDate changeCheckOut = now().plusDays(3L);

        // when
        ReservationDate changeReservationDate = reservationDate.changeCheckOut(changeCheckOut);

        // then
        assertAll(
            () -> assertThat(changeReservationDate.getCheckIn()).isEqualTo(checkIn),
            () -> assertThat(changeReservationDate.getCheckOut()).isEqualTo(changeCheckOut)
        );
    }

    @DisplayName("변경할 체크아웃날짜가 본래 체크아웃날짜보다 전이라면 예외를 발생한다.")
    @Test
    void changeCheckOut_isBefore() {
        // given
        LocalDate checkIn = now();
        LocalDate checkOut = now().plusDays(3L);
        ReservationDate reservationDate = new ReservationDate(checkIn, checkOut);

        // when
        // then
        assertThatThrownBy(() -> reservationDate.changeCheckOut(now().plusDays(1L)))
            .isInstanceOf(ReservationReduceDateException.class)
            .hasMessage("예약 변경은 예약 연장시에만 가능합니다.");
    }

}
