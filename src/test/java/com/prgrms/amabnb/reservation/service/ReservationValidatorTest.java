package com.prgrms.amabnb.reservation.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.prgrms.amabnb.common.vo.Email;
import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.vo.ReservationDate;
import com.prgrms.amabnb.reservation.exception.ReservationInvalidValueException;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.entity.UserRole;

class ReservationValidatorTest {

    private ReservationValidator reservationValidator;

    @BeforeEach
    void setUp() {
        reservationValidator = new ReservationValidator();
    }

    @DisplayName("총 금액이 숙소의 가격과 맞지 않다면 예외를 발생한다.")
    @Test
    void validateRoomPrice() {
        // given
        Room room = createRoom(200_000, 10);
        Reservation reservation = createReservation(room, 5L, 200_000, 5);

        // when
        // then
        assertThatThrownBy(() -> reservationValidator.validate(reservation))
            .isInstanceOf(ReservationInvalidValueException.class)
            .hasMessage("숙소 가격이 일치하지 않습니다.");
    }

    @DisplayName("예약 인원 수가 숙소 최대 인원을 넘는다면 예외를 발생한다.")
    @Test
    void validateMaxGuest() {
        // given
        Room room = createRoom(100_000, 5);
        Reservation reservation = createReservation(room, 3L, 300_000, 15);

        // when
        // then
        assertThatThrownBy(() -> reservationValidator.validate(reservation))
            .isInstanceOf(ReservationInvalidValueException.class)
            .hasMessage("숙소의 최대 인원을 넘을 수 없습니다.");
    }

    private Reservation createReservation(Room room, long period, int totalPrice, int totalGuest) {
        return Reservation.builder()
            .room(room)
            .guest(createUser())
            .totalGuest(totalGuest)
            .totalPrice(new Money(totalPrice))
            .reservationDate(new ReservationDate(LocalDate.now(), LocalDate.now().plusDays(period)))
            .build();
    }

    private Room createRoom(int price, int maxGuestNum) {
        return Room.builder()
            .name("별이 빛나는 밤")
            .maxGuestNum(maxGuestNum)
            .description("방 설명 입니다")
            .address(new RoomAddress("00000", "창원", "의창구"))
            .price(new Money(price))
            .roomOption(new RoomOption(1, 1, 1))
            .roomType(RoomType.APARTMENT)
            .roomScope(RoomScope.PRIVATE)
            .build();
    }

    private User createUser() {
        return User.builder()
            .oauthId("1")
            .provider("kakao")
            .name("아만드")
            .email(new Email("aramnd@gmail.com"))
            .userRole(UserRole.GUEST)
            .profileImgUrl("url")
            .build();
    }

}
