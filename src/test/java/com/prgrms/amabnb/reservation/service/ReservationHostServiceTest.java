package com.prgrms.amabnb.reservation.service;

import static com.prgrms.amabnb.reservation.entity.ReservationStatus.*;
import static java.time.LocalDate.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.amabnb.common.vo.Email;
import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.config.ApiTest;
import com.prgrms.amabnb.reservation.dto.response.ReservationInfoResponse;
import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.vo.ReservationDate;
import com.prgrms.amabnb.reservation.exception.ReservationNotHavePermissionException;
import com.prgrms.amabnb.reservation.exception.ReservationStatusException;
import com.prgrms.amabnb.reservation.repository.ReservationRepository;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import com.prgrms.amabnb.room.repository.RoomRepository;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.entity.UserRole;
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

    private Long guestId;

    private Long roomId;

    private Long hostId;

    private Long reservationId;

    @BeforeEach
    void setUp() {
        User guest = createUser();
        guestId = userRepository.save(guest).getId();
        User host = createHost();
        hostId = host.getId();
        Room room = createRoom(host);
        roomId = roomRepository.save(room).getId();
        reservationId = reservationRepository.save(createReservation(room, guest)).getId();
    }

    @DisplayName("호스트가 예약을 승인한다.")
    @Test
    void approve() {
        // given
        // when
        ReservationInfoResponse response = reservationHostService.approve(hostId, reservationId);

        // then
        assertThat(response.getReservationStatus()).isEqualTo(APPROVED);
    }

    @DisplayName("해당하는 호스트의 예약이 아닐 경우 승인할 수 없다.")
    @Test
    void approve_is_not_host() {
        assertThatThrownBy(() -> reservationHostService.approve(guestId, reservationId))
            .isInstanceOf(ReservationNotHavePermissionException.class)
            .hasMessage("해당 예약의 호스트가 아닙니다.");
    }

    @DisplayName("예약 상태를 변경할 수 없다면 승인할 수 없다.")
    @Test
    void approve_status_not_modifiable() {
        // given
        reservationHostService.approve(hostId, reservationId);

        // when
        // then
        assertThatThrownBy(() -> reservationHostService.approve(hostId, reservationId))
            .isInstanceOf(ReservationStatusException.class)
            .hasMessage("변경할 수 없는 예약입니다.");
    }

    @DisplayName("호스트가 예약을 취소한다.")
    @Test
    void cancelByHost() {
        // given
        // when
        reservationHostService.cancelByHost(hostId, reservationId);

        // then
        Reservation findReservation = reservationRepository.findById(reservationId).get();
        assertThat(findReservation.getReservationStatus()).isEqualTo(HOST_CANCELED);
    }

    @DisplayName("해당하는 호스트의 예약이 아닐 경우 승인할 수 없다.")
    @Test
    void cancelByHost_is_not_host() {
        assertThatThrownBy(() -> reservationHostService.cancelByHost(guestId, reservationId))
            .isInstanceOf(ReservationNotHavePermissionException.class)
            .hasMessage("해당 예약의 호스트가 아닙니다.");
    }

    private Reservation createReservation(Room room, User guest) {
        return Reservation.builder()
            .room(room)
            .guest(guest)
            .totalPrice(new Money(20_000))
            .totalGuest(1)
            .reservationDate(new ReservationDate(now(), now().plusDays(1L)))
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
            .build();

        room.setHost(host);
        return room;
    }

}
