package com.prgrms.amabnb.reservation.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.prgrms.amabnb.common.model.Money;
import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.vo.ReservationDate;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import com.prgrms.amabnb.room.repository.CreateRoomRepository;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.entity.UserRole;
import com.prgrms.amabnb.user.entity.vo.Email;
import com.prgrms.amabnb.user.entity.vo.PhoneNumber;
import com.prgrms.amabnb.user.repository.UserRepository;

@DataJpaTest
@ActiveProfiles("test")
@Import(QueryDslConfig.class)
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CreateRoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Room room;

    private static Stream<Arguments> provideReservationDate() {
        return Stream.of(
            Arguments.of(LocalDate.now(), LocalDate.now().plusDays(3L), true),
            Arguments.of(LocalDate.now().plusDays(5L), LocalDate.now().plusDays(10L), false),
            Arguments.of(LocalDate.now().plusDays(5L), LocalDate.now().plusDays(10L), false)
        );
    }

    @BeforeEach
    void setUp() {
        room = createRoom();
        createReservation(createGuest(), new ReservationDate(LocalDate.now(), LocalDate.now().plusDays(5L)));
        entityManager.flush();
        entityManager.clear();
    }

    @DisplayName("숙소가 해당 기간에 이미 예약이 되었는지 확인한다.")
    @ParameterizedTest(name = "reservationDate = {0}, result = {1}")
    @MethodSource("provideReservationDate")
    void existReservation(LocalDate checkIn, LocalDate checkOut, boolean result) {
        // given
        ReservationDate reservationDate = new ReservationDate(checkIn, checkOut);

        // when
        boolean isExists = reservationRepository.existReservation(room, reservationDate);

        // then
        assertThat(isExists).isEqualTo(result);
    }

    private User createGuest() {
        User user = User.builder()
            .oauthId("testOauthId")
            .provider("testProvider")
            .userRole(UserRole.GUEST)
            .name("testUser")
            .birth(LocalDate.of(2000, 1, 12))
            .email(new Email("asdsadsad@gmail.com"))
            .phoneNumber(new PhoneNumber("010-2312-1231"))
            .imageUrl("urlurlrurlrurlurlurl")
            .build();

        return userRepository.save(user);
    }

    private Room createRoom() {
        Room room = Room.builder()
            .maxGuestNum(1)
            .description("방 설명 입니다")
            .address(new RoomAddress("00000", "창원", "의창구"))
            .price(new Money(10_000))
            .roomOption(new RoomOption(1, 1, 1))
            .roomType(RoomType.APARTMENT)
            .roomScope(RoomScope.PRIVATE)
            .build();

        return roomRepository.save(room);
    }

    private Reservation createReservation(User guest, ReservationDate reservationDate) {
        Reservation reservation = Reservation.builder()
            .reservationDate(reservationDate)
            .maxGuest(3)
            .totalPrice(new Money(100_000))
            .room(room)
            .guest(guest)
            .build();

        return reservationRepository.save(reservation);
    }
}
