package com.prgrms.amabnb.reservation.service;

import static com.prgrms.amabnb.config.util.Fixture.*;
import static com.prgrms.amabnb.reservation.entity.ReservationStatus.*;
import static java.time.LocalDate.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.config.ApiTest;
import com.prgrms.amabnb.reservation.dto.request.SearchReservationsRequest;
import com.prgrms.amabnb.reservation.dto.response.ReservationInfoResponse;
import com.prgrms.amabnb.reservation.dto.response.ReservationResponseForHost;
import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.ReservationStatus;
import com.prgrms.amabnb.reservation.entity.vo.ReservationDate;
import com.prgrms.amabnb.reservation.exception.ReservationNotHavePermissionException;
import com.prgrms.amabnb.reservation.exception.ReservationStatusException;
import com.prgrms.amabnb.reservation.repository.ReservationRepository;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.repository.RoomRepository;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.repository.UserRepository;

class ReservationHostServiceTest extends ApiTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationHostService reservationHostService;

    private User guest;
    private User host;
    private Room room;
    private Long reservationId;

    @BeforeEach
    void setUp() {
        guest = userRepository.save(createUser("guest"));
        host = userRepository.save(createUser("host"));
        room = roomRepository.save(createRoom(host));
        reservationId = reservationRepository.save(createReservation(room, guest)).getId();
    }

    @DisplayName("호스트가 예약을 승인한다.")
    @Test
    void approve() {
        // given
        // when
        ReservationInfoResponse response = reservationHostService.approve(host.getId(), reservationId);

        // then
        assertThat(response.getReservationStatus()).isEqualTo(APPROVED);
    }

    @DisplayName("해당하는 호스트의 예약이 아닐 경우 승인할 수 없다.")
    @Test
    void approve_is_not_host() {
        assertThatThrownBy(() -> reservationHostService.approve(guest.getId(), reservationId))
            .isInstanceOf(ReservationNotHavePermissionException.class)
            .hasMessage("해당 예약의 호스트가 아닙니다.");
    }

    @DisplayName("예약 상태를 변경할 수 없다면 승인할 수 없다.")
    @Test
    void approve_status_not_modifiable() {
        // given
        reservationHostService.approve(host.getId(), reservationId);

        // when
        // then
        assertThatThrownBy(() -> reservationHostService.approve(host.getId(), reservationId))
            .isInstanceOf(ReservationStatusException.class)
            .hasMessage("변경할 수 없는 예약입니다.");
    }

    @DisplayName("호스트가 예약을 취소한다.")
    @Test
    void cancelByHost() {
        // given
        // when
        reservationHostService.cancelByHost(host.getId(), reservationId);

        // then
        Reservation findReservation = reservationRepository.findById(reservationId).get();
        assertThat(findReservation.getReservationStatus()).isEqualTo(HOST_CANCELED);
    }

    @DisplayName("해당하는 호스트의 예약이 아닐 경우 승인할 수 없다.")
    @Test
    void cancelByHost_is_not_host() {
        assertThatThrownBy(() -> reservationHostService.cancelByHost(guest.getId(), reservationId))
            .isInstanceOf(ReservationNotHavePermissionException.class)
            .hasMessage("해당 예약의 호스트가 아닙니다.");
    }

    @DisplayName("예약정보를 단건 조회한다.")
    @Test
    void getReservation() {
        // given
        // when
        ReservationResponseForHost reservation = reservationHostService.getReservation(host.getId(), reservationId);

        // then
        assertThat(reservation.getReservation().getId()).isEqualTo(reservationId);
    }

    @DisplayName("예약 정보들을 조회한다.")
    @Test
    void getReservations() {
        // given
        for (int i = 1; i < 11; i++) {
            reservationRepository.save(createReservationByDay(i));
        }
        Long lastReservationId = reservationRepository.save(createReservationByDay(12)).getId();
        int pageSize = 10;
        ReservationStatus status = PENDING;
        SearchReservationsRequest request = new SearchReservationsRequest(pageSize, status, null);

        // when
        List<ReservationResponseForHost> reservations = reservationHostService.getReservations(host.getId(), request);

        // then
        assertAll(
            () -> assertThat(reservations).hasSize(request.getPageSize()),
            () -> assertThat(reservations.get(0).getReservation().getId()).isEqualTo(lastReservationId),
            () -> assertThat(reservations).extracting("reservation")
                .extracting("reservationStatus")
                .containsOnly(status)
        );
    }

    private Reservation createReservationByDay(int day) {
        return Reservation.builder()
            .room(room)
            .guest(guest)
            .reservationStatus(PENDING)
            .totalPrice(new Money(20_000))
            .totalGuest(1)
            .reservationDate(new ReservationDate(now().plusDays(day), now().plusDays(day + 1)))
            .build();
    }

}
