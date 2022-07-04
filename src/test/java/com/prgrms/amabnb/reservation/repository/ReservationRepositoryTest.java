package com.prgrms.amabnb.reservation.repository;

import static com.prgrms.amabnb.reservation.entity.ReservationStatus.*;
import static java.time.LocalDate.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.amabnb.common.vo.Email;
import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.common.vo.PhoneNumber;
import com.prgrms.amabnb.config.RepositoryTest;
import com.prgrms.amabnb.reservation.dto.response.ReservationDateResponse;
import com.prgrms.amabnb.reservation.dto.response.ReservationDto;
import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.vo.ReservationDate;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import com.prgrms.amabnb.room.repository.RoomRepository;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.entity.UserRole;
import com.prgrms.amabnb.user.repository.UserRepository;

class ReservationRepositoryTest extends RepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    private Room room;

    private User guest;

    private User host;

    private static Stream<Arguments> provideReservationDate() {
        return Stream.of(
            Arguments.of(now(), now().plusDays(3L), true),
            Arguments.of(now().plusDays(5L), now().plusDays(10L), false),
            Arguments.of(now().plusDays(5L), now().plusDays(10L), false)
        );
    }

    @BeforeEach
    void setUp() {
        room = createRoom();
        guest = createGuest();
        createReservation(guest, new ReservationDate(now(), now().plusDays(5L)));
    }

    @DisplayName("숙소가 해당 기간에 이미 예약이 되었는지 확인한다.")
    @ParameterizedTest(name = "reservationDate = {0}, result = {1}")
    @MethodSource("provideReservationDate")
    void existReservation(LocalDate checkIn, LocalDate checkOut, boolean result) {
        // given
        ReservationDate reservationDate = new ReservationDate(checkIn, checkOut);

        // when
        boolean isExists = reservationRepository.existReservationByRoom(room, reservationDate);

        // then
        assertThat(isExists).isEqualTo(result);
    }

    @DisplayName("숙소의 예약 불가능 기간을 조회한다.")
    @Test
    void findImpossibleReservationDate() {
        // given
        LocalDate now = now();
        createReservation(guest, new ReservationDate(now.plusDays(10L), now.plusDays(15L)));
        createReservation(guest, new ReservationDate(now.plusMonths(1L).plusDays(3L), now.plusMonths(1L).plusDays(5L)));
        LocalDate endDate = now.plusMonths(1L);

        // when
        List<ReservationDateResponse> result = reservationRepository.findReservationDates(room.getId(),
            now, endDate);

        // then
        assertAll(
            () -> assertThat(result).hasSize(2),
            () -> assertThat(result).extracting("checkIn", "checkOut")
                .containsExactly(
                    tuple(now, now.plusDays(4L)),
                    tuple(now.plusDays(10L), now.plusDays(14L))
                )
        );
    }

    @DisplayName("게스트가 예약 정보들을 조회한다.")
    @Test
    void findReservationByGuestAndStatus() {
        // give
        for (int i = 0; i < 100; i++) {
            reservationRepository.save(
                createReservation(guest, new ReservationDate(now().plusDays(i), now().plusDays(i + 1))));
        }
        Long lastReservationId = null;
        int pageSize = 10;

        // when
        List<ReservationDto> reservations = reservationRepository.findReservationsByGuestAndStatus(
            lastReservationId, pageSize, guest, PENDING);

        // then
        assertAll(
            () -> assertThat(reservations).hasSize(pageSize),
            () -> assertThat(reservations).extracting("reservationStatus").containsOnly(PENDING)
        );
    }

    @DisplayName("호스트가 예약 정보들을 조회한다.")
    @Test
    void findReservationByHostAndStatus() {
        // give
        for (int i = 0; i < 100; i++) {
            reservationRepository.save(
                createReservation(guest, new ReservationDate(now().plusDays(i), now().plusDays(i + 1))));
        }
        Long lastReservationId = null;
        int pageSize = 10;

        // when
        List<ReservationDto> reservations = reservationRepository.findReservationsByHostAndStatus(
            lastReservationId, pageSize, host, null);

        // then
        assertThat(reservations).hasSize(pageSize);
    }

    private User createGuest() {
        User user = User.builder()
            .oauthId("testOauthId")
            .provider("testProvider")
            .userRole(UserRole.GUEST)
            .name("testUser")
            .email(new Email("asdsadsad@gmail.com"))
            .phoneNumber(new PhoneNumber("010-2312-1231"))
            .profileImgUrl("urlurlrurlrurlurlurl")
            .build();

        return userRepository.save(user);
    }

    private Room createRoom() {
        Room room = Room.builder()
            .name("별이 빛나는 밤")
            .maxGuestNum(1)
            .description("방 설명 입니다")
            .address(new RoomAddress("00000", "창원", "의창구"))
            .price(new Money(10_000))
            .roomOption(new RoomOption(1, 1, 1))
            .roomType(RoomType.APARTMENT)
            .roomScope(RoomScope.PRIVATE)
            .build();
        host = User.builder()
            .oauthId("testOauth")
            .provider("testProvider")
            .userRole(UserRole.GUEST)
            .name("testUser")
            .email(new Email("asdsadsasdad@gmail.com"))
            .profileImgUrl("urlurlrurlrurlurlurl")
            .build();

        userRepository.save(host);
        room.setHost(host);
        return roomRepository.save(room);
    }

    private Reservation createReservation(User guest, ReservationDate reservationDate) {
        Reservation reservation = Reservation.builder()
            .reservationDate(reservationDate)
            .totalGuest(3)
            .totalPrice(new Money(100_000))
            .room(room)
            .guest(guest)
            .build();

        return reservationRepository.save(reservation);
    }
}
