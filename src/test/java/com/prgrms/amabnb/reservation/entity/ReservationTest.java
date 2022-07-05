package com.prgrms.amabnb.reservation.entity;

import static com.prgrms.amabnb.reservation.entity.ReservationStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.prgrms.amabnb.common.vo.Email;
import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.reservation.entity.vo.ReservationDate;
import com.prgrms.amabnb.reservation.exception.ReservationInvalidValueException;
import com.prgrms.amabnb.reservation.exception.ReservationStatusException;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomImage;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.entity.UserRole;

class ReservationTest {

    @DisplayName("예약 객체를 생성한다.")
    @Test
    void create() {
        // given
        int totalGuest = 5;
        Money totalPrice = new Money(10_000);
        ReservationDate reservationDate = new ReservationDate(LocalDate.now(), LocalDate.now().plusDays(3L));
        Room room = createRoom();
        User guest = createUser("guest");

        // when
        Reservation reservation = Reservation.builder()
            .totalGuest(totalGuest)
            .totalPrice(totalPrice)
            .reservationDate(reservationDate)
            .reservationStatus(PENDING)
            .room(room)
            .guest(guest)
            .build();

        // then
        assertAll(
            () -> assertThat(reservation).isNotNull(),
            () -> assertThat(reservation.getReservationStatus()).isEqualTo(PENDING)
        );
    }

    @DisplayName("총 인원은 0미만이면 안된다.")
    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    void create_MaxGuest_Less_Then_Zero(int totalGuest) {
        assertThatThrownBy(() -> createReservationBuilder()
            .totalGuest(totalGuest)
            .build()
        ).isInstanceOf(ReservationInvalidValueException.class)
            .hasMessage("숙박 인원는 1미만일 수 없습니다.");
    }

    @DisplayName("총 가격은 비어있으면 안된다.")
    @Test
    void create_TotalPrice_Null() {
        assertThatThrownBy(() -> createReservationBuilder()
            .totalPrice(null)
            .build()
        ).isInstanceOf(ReservationInvalidValueException.class)
            .hasMessage("총 가격은 비어있을 수 없습니다.");
    }

    @DisplayName("숙소는 비어있으면 안된다.")
    @Test
    void create_Room_Null() {
        assertThatThrownBy(() -> createReservationBuilder()
            .room(null)
            .build()
        ).isInstanceOf(ReservationInvalidValueException.class)
            .hasMessage("숙소는 비어있을 수 없습니다.");
    }

    @DisplayName("게스트는 비어있으면 안된다.")
    @Test
    void create_Guest_Null() {
        assertThatThrownBy(() -> createReservationBuilder()
            .guest(null)
            .build()
        ).isInstanceOf(ReservationInvalidValueException.class)
            .hasMessage("게스트는 비어있을 수 없습니다.");
    }

    @DisplayName("예약날짜는 비어있으면 안된다.")
    @Test
    void create_ReservationDate_Null() {
        assertThatThrownBy(() -> createReservationBuilder()
            .reservationDate(null)
            .build()
        ).isInstanceOf(ReservationInvalidValueException.class)
            .hasMessage("예약 날짜는 비어있을 수 없습니다.");
    }

    @DisplayName("예약 상태가 PENDING이면 예약 상태 변경이 가능하다.")
    @ParameterizedTest
    @CsvSource(value = {"APPROVED", "HOST_CANCELED", "GUEST_CANCELED"})
    void changeStatus_PENDING(ReservationStatus status) {
        // given
        Reservation reservation = createReservationBuilder().build();

        // when
        reservation.changeStatus(status);

        // then
        assertThat(reservation.getReservationStatus()).isEqualTo(status);
    }

    @DisplayName("예약 상태가 PENDING이 아니라면 예약 상태 변경 불가능하다.")
    @ParameterizedTest
    @CsvSource(value = {"APPROVED", "HOST_CANCELED", "GUEST_CANCELED"})
    void changeStatus_not_PENDING(ReservationStatus status) {
        // given
        Reservation reservation = createReservationBuilder().build();
        reservation.changeStatus(APPROVED);

        // when
        // then
        assertThatThrownBy(() -> reservation.changeStatus(status))
            .isInstanceOf(ReservationStatusException.class)
            .hasMessage("변경할 수 없는 예약입니다.");
    }

    private Reservation.ReservationBuilder createReservationBuilder() {
        Room room = createRoom();
        User user = createUser("guest");

        return Reservation.builder()
            .totalGuest(5)
            .totalPrice(new Money(10_000))
            .reservationDate(new ReservationDate(LocalDate.now(), LocalDate.now().plusDays(3L)))
            .reservationStatus(PENDING)
            .room(room)
            .guest(user);
    }

    private Room createRoom() {
        return Room.builder()
            .name("별이 빛나는 밤")
            .maxGuestNum(1)
            .description("방 설명 입니다")
            .address(new RoomAddress("00000", "창원", "의창구"))
            .price(new Money(1_000))
            .roomOption(new RoomOption(1, 1, 1))
            .roomType(RoomType.APARTMENT)
            .roomScope(RoomScope.PRIVATE)
            .roomImages(List.of(createRoomImage()))
            .build();
    }

    private RoomImage createRoomImage() {
        return new RoomImage("aaa");
    }

    private User createUser(String name) {
        return User.builder()
            .oauthId(name)
            .provider(name)
            .userRole(UserRole.GUEST)
            .name(name)
            .email(new Email(name + "@gmail.com"))
            .profileImgUrl("urlurlrurlrurlurlurl")
            .build();
    }

}
