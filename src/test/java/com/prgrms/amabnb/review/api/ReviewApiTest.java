package com.prgrms.amabnb.review.api;

import com.prgrms.amabnb.common.vo.Email;
import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.config.ApiTest;
import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.ReservationStatus;
import com.prgrms.amabnb.reservation.entity.vo.ReservationDate;
import com.prgrms.amabnb.reservation.repository.ReservationRepository;
import com.prgrms.amabnb.review.dto.request.CreateReviewRequest;
import com.prgrms.amabnb.review.repository.ReviewRepository;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import com.prgrms.amabnb.room.repository.RoomRepository;
import com.prgrms.amabnb.token.service.TokenService;
import com.prgrms.amabnb.user.dto.response.UserRegisterResponse;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.entity.UserRole;
import com.prgrms.amabnb.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static com.prgrms.amabnb.review.api.ReviewApiTest.Fixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReviewApiTest extends ApiTest {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    public String createToken(User user) {
        return "Bearer " + tokenService.createToken(
                new UserRegisterResponse(user.getId(), "ROLE_GUEST")).accessToken();
    }

    static class Fixture {
        static User createUser(String name) {
            return User.builder()
                    .name(name)
                    .userRole(UserRole.GUEST)
                    .provider("kakao")
                    .oauthId("oauthId")
                    .email(new Email(UUID.randomUUID() + "@naver.com"))
                    .profileImgUrl("something url")
                    .build();
        }

        static Reservation createReservation(User guest, Room room) {
            var reservation = Reservation.builder()
                    .id(1L)
                    .reservationDate(new ReservationDate(LocalDate.now(), LocalDate.now().plusDays(3L)))
                    .totalGuest(1)
                    .totalPrice(new Money(1000))
                    .room(room)
                    .guest(guest)
                    .build();
            return reservation;
        }

        static Room createRoom(User user) {
            var room = Room.builder()
                    .id(1L)
                    .name("방이름")
                    .price(new Money(1000))
                    .description("방설명")
                    .maxGuestNum(10)
                    .address(new RoomAddress("12345", "address", "detailAddress"))
                    .roomOption(new RoomOption(1, 1, 1))
                    .roomType(RoomType.HOUSE)
                    .roomScope(RoomScope.PUBLIC)
                    .host(user)
                    .build();
            return room;
        }

    }

    @Nested
    @DisplayName("게스트는 예약했던 숙소에 리뷰를 작성할 수 있다 #68")
    class CreateReview {
        User givenGuest;
        User givenHost;
        Room givenRoom;
        Reservation givenReservation;
        String givenAccessToken;
        CreateReviewRequest givenReviewRequest;

        @BeforeEach
        @Transactional
        void setGiven() {
            givenReviewRequest = new CreateReviewRequest("content", 5);
            givenGuest = userRepository.save(createUser("su"));
            givenHost = userRepository.save(createUser("bin"));

            givenRoom = roomRepository.saveAndFlush(createRoom(givenHost));
            givenReservation = reservationRepository.saveAndFlush(createReservation(givenGuest, givenRoom));
            givenAccessToken = createToken(givenGuest);
        }

        @Test
        @DisplayName("리뷰를 작성할 수 있습니다.")
        void postReview() throws Exception {
            assertThat(reviewRepository.count()).isZero();
            givenReservation.changeStatus(ReservationStatus.COMPLETED);
            reservationRepository.saveAndFlush(givenReservation);

            mockMvc.perform(post("/reservations/{reservationId}/reviews", givenReservation.getId())
                            .header(HttpHeaders.AUTHORIZATION, givenAccessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(givenReviewRequest)))
                    .andExpect(status().isCreated())
                    .andDo(print());
            assertThat(reviewRepository.count()).isOne();
        }

        @ParameterizedTest
        @DisplayName("숙소를 방문을 완료(COMPLETED)한 후에 리뷰를 작성할 수 있습니다.")
        @EnumSource(value = ReservationStatus.class, names = {"PENDING", "APPROVED", "GUEST_CANCELED", "HOST_CANCELED"})
        void exception1(ReservationStatus status) throws Exception {
            var illegalReservation = reservationRepository.saveAndFlush(createReservation(givenGuest, givenRoom));
            illegalReservation.changeStatus(status);

            mockMvc.perform(post("/reservations/{reservationId}/reviews", illegalReservation.getId())
                            .header(HttpHeaders.AUTHORIZATION, givenAccessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(givenReviewRequest)))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @DisplayName("리뷰는 예약 한 건당 한 개만 작성할 수 있습니다.")
        void exception2() throws Exception {
            givenReservation.changeStatus(ReservationStatus.COMPLETED);
            reservationRepository.saveAndFlush(givenReservation);

            mockMvc.perform(post("/reservations/{reservationId}/reviews", givenReservation.getId())
                            .header(HttpHeaders.AUTHORIZATION, givenAccessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(givenReviewRequest)))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/reservations/{reservationId}/reviews", givenReservation.getId())
                            .header(HttpHeaders.AUTHORIZATION, givenAccessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(givenReviewRequest)))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @DisplayName("예약자 본인만 리뷰를 작성할 수 있습니다.")
        void exception3() throws Exception {
            givenReservation.changeStatus(ReservationStatus.COMPLETED);
            reservationRepository.saveAndFlush(givenReservation);
            var illegalUser = userRepository.saveAndFlush(createUser("illegal"));
            var illegalToken = createToken(illegalUser);
            mockMvc.perform(post("/reservations/{reservationId}/reviews", givenReservation.getId())
                            .header(HttpHeaders.AUTHORIZATION, illegalToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(givenReviewRequest)))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }
}
