package com.prgrms.amabnb.reservation.service;

import static com.prgrms.amabnb.reservation.entity.ReservationStatus.*;
import static java.time.LocalDate.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.amabnb.common.vo.Email;
import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.config.ApiTest;
import com.prgrms.amabnb.reservation.dto.request.CreateReservationRequest;
import com.prgrms.amabnb.reservation.dto.request.ReservationDateRequest;
import com.prgrms.amabnb.reservation.dto.request.ReservationUpdateRequest;
import com.prgrms.amabnb.reservation.dto.request.SearchReservationsRequest;
import com.prgrms.amabnb.reservation.dto.response.ReservationDateResponse;
import com.prgrms.amabnb.reservation.dto.response.ReservationInfoResponse;
import com.prgrms.amabnb.reservation.dto.response.ReservationResponseForGuest;
import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.ReservationStatus;
import com.prgrms.amabnb.reservation.exception.AlreadyReservationRoomException;
import com.prgrms.amabnb.reservation.exception.ReservationInvalidValueException;
import com.prgrms.amabnb.reservation.exception.ReservationNotHavePermissionException;
import com.prgrms.amabnb.reservation.repository.ReservationRepository;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomImage;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import com.prgrms.amabnb.room.exception.RoomNotFoundException;
import com.prgrms.amabnb.room.repository.RoomRepository;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.entity.UserRole;
import com.prgrms.amabnb.user.exception.UserNotFoundException;
import com.prgrms.amabnb.user.repository.UserRepository;

class ReservationGuestServiceTest extends ApiTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationGuestService reservationGuestService;

    private User guest;

    private Room room;

    private User host;

    @BeforeEach
    void setUp() {
        guest = userRepository.save(createUser());
        host = createHost();
        room = roomRepository.save(createRoom(host));
    }

    @DisplayName("예약을 생성한다.")
    @Test
    void create_reservation() {
        // given
        CreateReservationRequest request = createReservationRequest(3, 30_000, room.getId());

        // when
        ReservationResponseForGuest response = reservationGuestService.createReservation(guest.getId(), request);

        // then
        assertAll(
            () -> assertThat(response.getReservation().getId()).isPositive(),
            () -> assertThat(response.getReservation().getReservationStatus()).isEqualTo(PENDING)
        );
    }

    @DisplayName("총 금액이 숙소의 가격과 맞지 않다면 예외를 발생한다.")
    @Test
    void create_reservation_invalid_price() {
        // given
        CreateReservationRequest request = createReservationRequest(3, 40_000, room.getId());

        // when
        // then
        assertThatThrownBy(() -> reservationGuestService.createReservation(guest.getId(), request))
            .isInstanceOf(ReservationInvalidValueException.class)
            .hasMessage("숙소 가격이 일치하지 않습니다.");
    }

    @DisplayName("예약 인원 수가 숙소 최대 인원을 넘는다면 예외를 발생한다.")
    @Test
    void create_reservation_over_max_guest() {
        // given
        CreateReservationRequest request = createReservationRequest(20, 30_000, room.getId());

        // when
        // then
        assertThatThrownBy(() -> reservationGuestService.createReservation(guest.getId(), request))
            .isInstanceOf(ReservationInvalidValueException.class)
            .hasMessage("숙소의 최대 인원을 넘을 수 없습니다.");
    }

    @DisplayName("예약 기간에 이미 예약된 숙소라면 예외를 발생한다.")
    @Test
    void create_reservation_already_reserved_room() {
        // given
        CreateReservationRequest request = createReservationRequest(3, 30_000, room.getId());
        reservationGuestService.createReservation(guest.getId(), request);

        // when
        // then
        assertThatThrownBy(() -> reservationGuestService.createReservation(guest.getId(), request))
            .isInstanceOf(AlreadyReservationRoomException.class)
            .hasMessage("해당 숙소가 이미 예약된 기간입니다.");
    }

    @DisplayName("숙소가 존재하지 않다면 예외를 발생한다.")
    @Test
    void create_reservation_not_found_room() {
        // given
        CreateReservationRequest request = createReservationRequest(3, 30_000, 100L);

        // when
        // then
        assertThatThrownBy(() -> reservationGuestService.createReservation(guest.getId(), request))
            .isInstanceOf(RoomNotFoundException.class)
            .hasMessage("존재하지 않는 숙소입니다");
    }

    @DisplayName("유저가 존재하지 않다면 예외를 발생한다.")
    @Test
    void create_reservation_not_found_guest() {
        // given
        CreateReservationRequest request = createReservationRequest(3, 30_000, room.getId());

        // when
        // then
        assertThatThrownBy(() -> reservationGuestService.createReservation(100L, request))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessage("존재하지 않는 유저입니다");
    }

    @DisplayName("게스트가 예약을 수정한다.")
    @Test
    void modify() {
        // given
        CreateReservationRequest request = createReservationRequest(3, 30_000, room.getId());
        Long reservationId = reservationGuestService.createReservation(guest.getId(), request).getReservation().getId();
        ReservationUpdateRequest updateRequest = new ReservationUpdateRequest(now().plusDays(5L), 5, 20_000);

        // when
        ReservationInfoResponse reservation = reservationGuestService.modify(guest.getId(), reservationId,
            updateRequest).getReservation();

        // then
        assertAll(
            () -> assertThat(reservation.getTotalPrice()).isEqualTo(50_000),
            () -> assertThat(reservation.getTotalGuest()).isEqualTo(5),
            () -> assertThat(reservation.getCheckOut()).isEqualTo(now().plusDays(5L))
        );
    }

    @DisplayName("예약 불가능한 날짜를 조회한다.")
    @Test
    void getImpossibleReservationDates() {
        // given
        reservationGuestService.createReservation(guest.getId(), createReservationRequest(3, 30_000, room.getId()));
        ReservationDateRequest request = new ReservationDateRequest(now(), now().plusMonths(1L));

        // when
        List<ReservationDateResponse> reservationDates = reservationGuestService.getReservationDates(room.getId(),
            request);

        // then
        assertAll(
            () -> assertThat(reservationDates).hasSize(1),
            () -> assertThat(reservationDates).extracting("checkIn", "checkOut")
                .containsExactly(tuple(now(), now().plusDays(2L)))
        );
    }

    @DisplayName("게스트가 예약을 취소한다.")
    @Test
    void cancelByGuest() {
        // given
        Long reservationId = reservationGuestService.createReservation(guest.getId(), createReservationByDay(11))
            .getReservation()
            .getId();

        // when
        reservationGuestService.cancel(guest.getId(), reservationId);

        // then
        Reservation findReservation = reservationRepository.findById(reservationId).get();
        assertThat(findReservation.getReservationStatus()).isEqualTo(GUEST_CANCELED);
    }

    @DisplayName("해당하는 게스트의 예약이 아닐 경우 취소할 수 없다.")
    @Test
    void cancelByGuest_is_not_guest() {
        // given
        Long reservationId = reservationGuestService.createReservation(guest.getId(), createReservationByDay(11))
            .getReservation()
            .getId();

        // when
        // then
        assertThatThrownBy(() -> reservationGuestService.cancel(host.getId(), reservationId))
            .isInstanceOf(ReservationNotHavePermissionException.class)
            .hasMessage("해당 예약의 게스트가 아닙니다.");
    }

    @DisplayName("예약정보를 단건 조회한다.")
    @Test
    void getReservation() {
        // given
        Long reservationId = reservationGuestService.createReservation(guest.getId(), createReservationByDay(11))
            .getReservation()
            .getId();

        // when
        ReservationResponseForGuest reservation = reservationGuestService.getReservation(guest.getId(), reservationId);

        // then
        assertThat(reservation.getReservation().getId()).isEqualTo(reservationId);
    }

    @DisplayName("예약 정보들을 조회한다.")
    @Test
    void getReservations() {
        // given
        for (int i = 0; i < 10; i++) {
            reservationGuestService.createReservation(guest.getId(), createReservationByDay(i));
        }
        Long lastReservationId = reservationGuestService.createReservation(guest.getId(), createReservationByDay(11))
            .getReservation()
            .getId();
        int pageSize = 10;
        ReservationStatus status = PENDING;
        SearchReservationsRequest request = new SearchReservationsRequest(pageSize, status, lastReservationId);

        // when
        List<ReservationResponseForGuest> reservations = reservationGuestService.getReservations(guest.getId(),
            request);

        // then
        assertAll(
            () -> assertThat(reservations).hasSize(request.getPageSize()),
            () -> assertThat(reservations.get(0).getReservation().getId()).isEqualTo(lastReservationId - 1),
            () -> assertThat(reservations).extracting("reservation")
                .extracting("reservationStatus")
                .containsOnly(status)
        );
    }

    private CreateReservationRequest createReservationRequest(int totalGuest, int totalPrice, Long roomId) {
        return CreateReservationRequest.builder()
            .checkIn(now())
            .checkOut(now().plusDays(3L))
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

    private User createHost() {
        User user = User.builder()
            .oauthId("host")
            .provider("host")
            .userRole(UserRole.HOST)
            .name("host")
            .email(new Email("host@gmail.com"))
            .profileImgUrl("urlurlrurlrurlurlurl")
            .build();

        return userRepository.save(user);
    }

    private Room createRoom(User host) {
        Room room = Room.builder()
            .name("별이 빛나는 밤")
            .maxGuestNum(10)
            .description("방 설명 입니다")
            .address(new RoomAddress("00000", "창원", "의창구"))
            .price(new Money(10_000))
            .roomOption(new RoomOption(1, 1, 1))
            .roomType(RoomType.APARTMENT)
            .roomScope(RoomScope.PRIVATE)
            .host(host)
            .roomImages(List.of(createRoomImage()))
            .build();

        return room;
    }

    private RoomImage createRoomImage() {
        return new RoomImage("aaa");
    }

    private CreateReservationRequest createReservationByDay(int day) {
        return CreateReservationRequest.builder()
            .checkIn(now().plusDays(day))
            .checkOut(now().plusDays(day + 1))
            .totalGuest(3)
            .totalPrice(10_000)
            .roomId(room.getId())
            .build();
    }

}
