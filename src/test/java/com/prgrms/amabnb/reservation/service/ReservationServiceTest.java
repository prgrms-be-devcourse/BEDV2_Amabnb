package com.prgrms.amabnb.reservation.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.common.vo.Email;
import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.reservation.dto.request.CreateReservationRequest;
import com.prgrms.amabnb.reservation.dto.response.ReservationResponseForGuest;
import com.prgrms.amabnb.reservation.entity.ReservationStatus;
import com.prgrms.amabnb.reservation.exception.AlreadyReservationRoomException;
import com.prgrms.amabnb.reservation.exception.AlreadyReservationUserException;
import com.prgrms.amabnb.reservation.exception.ReservationInvalidValueException;
import com.prgrms.amabnb.reservation.repository.ReservationRepository;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import com.prgrms.amabnb.room.exception.RoomNotFoundException;
import com.prgrms.amabnb.room.repository.CreateRoomRepository;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.entity.UserRole;
import com.prgrms.amabnb.user.exception.UserNotFoundException;
import com.prgrms.amabnb.user.repository.UserRepository;

@SpringBootTest
@Transactional
class ReservationServiceTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CreateRoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    private ReservationService reservationService;

    private Long guestId;

    private Long roomId;

    @BeforeEach
    void setUp() {
        guestId = userRepository.save(createUser()).getId();
        roomId = roomRepository.save(createRoom()).getId();
        reservationService = new ReservationService(reservationRepository, roomRepository, userRepository);
    }

    @DisplayName("예약을 생성한다.")
    @Test
    void create_reservation() {
        // given
        CreateReservationRequest request = createReservationRequest(3, 30_000, roomId);

        // when
        ReservationResponseForGuest response = reservationService.createReservation(guestId, request);

        // then
        assertAll(
            () -> assertThat(response.getId()).isPositive(),
            () -> assertThat(response.getReservationStatus()).isEqualTo(ReservationStatus.PENDING)
        );
    }

    @DisplayName("총 금액이 숙소의 가격과 맞지 않다면 예외를 발생한다.")
    @Test
    void create_reservation_invalid_price() {
        // given
        CreateReservationRequest request = createReservationRequest(3, 40_000, roomId);

        // when
        // then
        assertThatThrownBy(() -> reservationService.createReservation(guestId, request))
            .isInstanceOf(ReservationInvalidValueException.class)
            .hasMessage("숙소 가격이 일치하지 않습니다.");
    }

    @DisplayName("예약 인원 수가 숙소 최대 인원을 넘는다면 예외를 발생한다.")
    @Test
    void create_reservation_over_max_guest() {
        // given
        CreateReservationRequest request = createReservationRequest(20, 30_000, roomId);

        // when
        // then
        assertThatThrownBy(() -> reservationService.createReservation(guestId, request))
            .isInstanceOf(ReservationInvalidValueException.class)
            .hasMessage("숙소의 최대 인원을 넘을 수 없습니다.");
    }

    @DisplayName("예약 기간에 이미 예약된 숙소라면 예외를 발생한다.")
    @Test
    void create_reservation_already_reserved_room() {
        // given
        CreateReservationRequest request = createReservationRequest(3, 30_000, roomId);
        reservationService.createReservation(guestId, request);

        // when
        // then
        assertThatThrownBy(() -> reservationService.createReservation(guestId, request))
            .isInstanceOf(AlreadyReservationRoomException.class)
            .hasMessage("해당 숙소가 이미 예약된 기간입니다.");
    }

    @DisplayName("예약 기간에 게스트가 이미 예약한 숙소가 있다면 예외를 발생한다.")
    @Test
    void create_reservation_already_reserved_guest() {
        // given
        reservationService.createReservation(guestId, createReservationRequest(3, 30_000, roomId));
        Long anotherRoomId = roomRepository.save(createRoom()).getId();
        CreateReservationRequest request = createReservationRequest(3, 30_000, anotherRoomId);

        // when
        // then
        assertThatThrownBy(() -> reservationService.createReservation(guestId, request))
            .isInstanceOf(AlreadyReservationUserException.class)
            .hasMessage("귀하가 이미 숙소를 예약한 기간입니다.");
    }

    @DisplayName("숙소가 존재하지 않다면 예외를 발생한다.")
    @Test
    void create_reservation_not_found_room() {
        // given
        CreateReservationRequest request = createReservationRequest(3, 30_000, 100L);

        // when
        // then
        assertThatThrownBy(() -> reservationService.createReservation(guestId, request))
            .isInstanceOf(RoomNotFoundException.class)
            .hasMessage("해당 숙소를 찾을 수 없습니다.");
    }

    @DisplayName("유저가 존재하지 않다면 예외를 발생한다.")
    @Test
    void create_reservation_not_found_guest() {
        // given
        CreateReservationRequest request = createReservationRequest(3, 30_000, roomId);

        // when
        // then
        assertThatThrownBy(() -> reservationService.createReservation(100L, request))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessage("존재 하지 않는 유저입니다.");
    }

    private CreateReservationRequest createReservationRequest(int totalGuest, int totalPrice, Long roomId) {
        return CreateReservationRequest.builder()
            .checkIn(LocalDate.now())
            .checkOut(LocalDate.now().plusDays(3L))
            .totalGuest(totalGuest)
            .totalPrice(totalPrice)
            .roomId(roomId)
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

    private Room createRoom() {
        return Room.builder()
            .name("별이 빛나는 밤")
            .maxGuestNum(10)
            .description("방 설명 입니다")
            .address(new RoomAddress("00000", "창원", "의창구"))
            .price(new Money(10_000))
            .roomOption(new RoomOption(1, 1, 1))
            .roomType(RoomType.APARTMENT)
            .roomScope(RoomScope.PRIVATE)
            .build();
    }

}
