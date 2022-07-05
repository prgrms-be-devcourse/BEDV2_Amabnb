package com.prgrms.amabnb.review.api;

import static com.prgrms.amabnb.config.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.config.ApiTest;
import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.ReservationStatus;
import com.prgrms.amabnb.reservation.repository.ReservationRepository;
import com.prgrms.amabnb.review.dto.request.CreateReviewRequest;
import com.prgrms.amabnb.review.dto.request.EditReviewRequest;
import com.prgrms.amabnb.review.entity.Review;
import com.prgrms.amabnb.review.repository.ReviewRepository;
import com.prgrms.amabnb.room.repository.RoomRepository;
import com.prgrms.amabnb.user.repository.UserRepository;

class ReviewApiTest extends ApiTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private RoomRepository roomRepository;

    private Reservation givenReservation;
    private String givenGuestAccessToken;

    @BeforeEach
    @Transactional
    void setBasicGiven() throws Exception {
        var givenGuest = userRepository.save(createUser("guest"));
        var givenHost = userRepository.save(createUser("host"));
        var givenRoom = roomRepository.save(createRoom(givenHost));

        givenGuestAccessToken = 로그인_요청(givenGuest.getName());
        givenReservation = reservationRepository.findById(
            extractId(예약_요청(givenGuestAccessToken, makeCreateReservationRequest(givenRoom)))).get();
    }

    private ResultActions when_리뷰_작성(Long reservationId, String userAccessToken,
        CreateReviewRequest createReviewDto) throws Exception {
        return mockMvc.perform(post("/reservations/{reservationId}/reviews", reservationId)
            .header(HttpHeaders.AUTHORIZATION, userAccessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(createReviewDto)));
    }

    private ResultActions when_리뷰_삭제(String userAccessToken, Long reviewId) throws Exception {
        return mockMvc.perform(delete("/reviews/{reviewId}", reviewId)
            .header(HttpHeaders.AUTHORIZATION, userAccessToken));
    }

    private ResultActions when_리뷰_수정(String userAccessToken, Long reviewId,
        EditReviewRequest editReviewDto) throws Exception {
        return mockMvc.perform(post("/reviews/{reviewId}", reviewId)
            .header(HttpHeaders.AUTHORIZATION, userAccessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(editReviewDto)));
    }

    @Nested
    @DisplayName("게스트는 예약했던 숙소에 리뷰를 작성할 수 있다 #68")
    class CreateReview {
        CreateReviewRequest givenReviewRequest;

        @BeforeEach
        void setAdditionalGiven() {
            givenReviewRequest = new CreateReviewRequest("content", 5);
        }

        @Test
        @DisplayName("리뷰를 작성할 수 있다.")
        void postReview() throws Exception {
            givenReservation.changeStatus(ReservationStatus.COMPLETED);
            reservationRepository.save(givenReservation);

            when_리뷰_작성(givenReservation.getId(), givenGuestAccessToken, givenReviewRequest)
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("/reviews/*"))
                .andDo(print());

        }

        @ParameterizedTest
        @DisplayName("숙소를 방문을 완료(COMPLETED)한 후에 리뷰를 작성할 수 있습니다.")
        @EnumSource(value = ReservationStatus.class, names = {"PENDING", "APPROVED", "GUEST_CANCELED", "HOST_CANCELED"})
        void exception1(ReservationStatus status) throws Exception {
            var errorMessage = "숙소 방문 완료 후 리뷰를 작성할 수 있습니다.";
            givenReservation.changeStatus(status);
            reservationRepository.save(givenReservation);

            when_리뷰_작성(givenReservation.getId(), givenGuestAccessToken, givenReviewRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andDo(print());
        }

        @Test
        @DisplayName("리뷰는 예약 한 건당 한 개만 작성할 수 있습니다.")
        void exception2() throws Exception {
            var errorMessage = "이미 작성한 예약 건 입니다.";

            givenReservation.changeStatus(ReservationStatus.COMPLETED);
            reservationRepository.save(givenReservation);

            var firstReview = when_리뷰_작성(givenReservation.getId(), givenGuestAccessToken, givenReviewRequest);
            firstReview.andExpect(status().isCreated());

            var secondReview = when_리뷰_작성(givenReservation.getId(), givenGuestAccessToken, givenReviewRequest);
            secondReview.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andDo(print());
        }

        @Test
        @DisplayName("예약자 본인만 리뷰를 작성할 수 있습니다.")
        void exception3() throws Exception {
            var errorMessage = "리뷰에 대한 권한이 존재하지 않습니다.";

            givenReservation.changeStatus(ReservationStatus.COMPLETED);
            reservationRepository.save(givenReservation);

            var illegalToken = 로그인_요청(createUserProfile("illegal"));

            when_리뷰_작성(givenReservation.getId(), illegalToken, givenReviewRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andDo(print());
        }
    }

    @Nested
    @DisplayName("게스트는 본인이 작성한 리뷰를 삭제할 수 있다 #82")
    class DeleteReview {
        Review givenReview;

        @BeforeEach
        @Transactional
        void setAdditionalGiven() {
            givenReview = reviewRepository.save(new Review(1L, "content", 4, givenReservation));
        }

        @Test
        @DisplayName("리뷰를 삭제할 수 있다")
        void deleteReview() throws Exception {
            assertThat(reviewRepository.count()).isOne();

            when_리뷰_삭제(givenGuestAccessToken, givenReview.getId())
                .andExpect(status().isNoContent())
                .andDo(print());

            assertThat(reviewRepository.count()).isZero();
        }

        @Test
        @DisplayName("예약자 본인만 리뷰를 삭제할 수 있다")
        void deleteReviewno() throws Exception {
            var errorMessage = "리뷰에 대한 권한이 존재하지 않습니다.";
            assertThat(reviewRepository.count()).isOne();

            var illegalToken = 로그인_요청(createUserProfile("illegal-user"));

            when_리뷰_삭제(illegalToken, givenReview.getId())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andDo(print());

            assertThat(reviewRepository.count()).isOne();
        }
    }

    @Nested
    @DisplayName("게스트는 본인이 작성한 리뷰를 수정할 수 있다 #81")
    class EditReview {

        Review givenReview;
        EditReviewRequest givenEditDto;

        @BeforeEach
        @Transactional
        void setAdditionalGiven() {
            givenReview = reviewRepository.save(new Review(1L, "content", 4, givenReservation));
            givenEditDto = new EditReviewRequest("content", 2);
        }

        @Test
        @DisplayName("리뷰를 수정할 수 있다")
        void postReview() throws Exception {
            when_리뷰_수정(givenGuestAccessToken, givenReview.getId(), givenEditDto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value(givenEditDto.getContent()))
                .andExpect(jsonPath("$.data.score").value(givenEditDto.getScore()))
                .andDo(print());
        }

        @Test
        @DisplayName("본인이 작성하지 않은 리뷰는 수정할 수 없다")
        void noPermission() throws Exception {
            var errorMessage = "리뷰에 대한 권한이 존재하지 않습니다.";
            var illegalToken = 로그인_요청(createUserProfile("illegal-user"));

            when_리뷰_수정(illegalToken, givenReview.getId(), givenEditDto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andDo(print());
        }
    }
}
