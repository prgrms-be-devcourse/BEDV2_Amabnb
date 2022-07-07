package com.prgrms.amabnb.review.service;

import static com.prgrms.amabnb.config.util.Fixture.*;
import static com.prgrms.amabnb.reservation.entity.ReservationStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import com.prgrms.amabnb.reservation.dto.response.ReservationReviewResponse;
import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.service.ReservationGuestService;
import com.prgrms.amabnb.review.dto.request.CreateReviewRequest;
import com.prgrms.amabnb.review.dto.request.EditReviewRequest;
import com.prgrms.amabnb.review.dto.request.PageReviewRequest;
import com.prgrms.amabnb.review.dto.request.SearchReviewRequest;
import com.prgrms.amabnb.review.dto.response.EditReviewResponse;
import com.prgrms.amabnb.review.dto.response.SearchReviewResponse;
import com.prgrms.amabnb.review.entity.Review;
import com.prgrms.amabnb.review.repository.ReviewRepository;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReservationGuestService reservationGuestService;

    private Reservation givenReservation;

    @BeforeEach
    void setBasicGiven() {
        var givenGuest = createUserWithId("guest");
        var givenHost = createUser("host");
        givenReservation = createReservationWithId(createRoom(givenHost), givenGuest);
        givenReservation.changeStatus(COMPLETED);
    }

    @Nested
    @DisplayName("게스트는 예약했던 숙소에 리뷰를 작성할 수 있다 #68")
    class CreateReview {
        Review givenReview;
        ReservationReviewResponse givenReservationDto;

        @BeforeEach
        void setAdditionalGivne() {
            givenReview = createReviewWithId(givenReservation);
            givenReservationDto = ReservationReviewResponse.from(givenReservation);
        }

        @Test
        @DisplayName("리뷰를 작성한다")
        void createUserReview() {
            var givenRequestDto = new CreateReviewRequest("content", 2);

            when(reservationGuestService.findById(anyLong())).thenReturn(givenReservationDto);
            when(reviewRepository.save(any(Review.class))).thenReturn(givenReview);
            when(reviewRepository.existsByReservationId(anyLong())).thenReturn(false);

            var guest = givenReservation.getGuest();
            var result = reviewService.createReview(guest.getId(), givenReservation.getId(), givenRequestDto);

            then(reservationGuestService).should(times(1)).findById(anyLong());
            then(reviewRepository).should(times(1)).existsByReservationId(anyLong());
            then(reviewRepository).should(times(1)).save(any(Review.class));
            assertThat(result).isEqualTo(givenReview.getId());
        }
    }

    @Nested
    @DisplayName("게스트는 본인이 작성한 리뷰 목록을 조회할 수 있다 #70")
    class SearchMyReviews {

        @Test
        @DisplayName("개인 리뷰를 검색 조건에 따라 조회한다")
        void search() {
            var givenUserId = 1L;

            var givenCondition = new SearchReviewRequest(2);
            var givenPageable = new PageReviewRequest(10, 10);

            var givenResult = List.of(new SearchReviewResponse(2, "content"));

            when(reviewRepository
                .findMyReviewByCondition(anyLong(), any(SearchReviewRequest.class), any(Pageable.class)))
                .thenReturn(givenResult);

            reviewService.searchMyReviews(givenUserId, givenCondition, givenPageable.of());

            then(reviewRepository).should(times(1))
                .findMyReviewByCondition(givenUserId, givenCondition, givenPageable.of());
        }

    }

    @Nested
    @DisplayName("게스트는 숙소의 리뷰를 조회할 수 있다 #67")
    class SearchRoomReviews {

        @Test
        @DisplayName("숙소 리뷰를 검색 조건에 따라 조회한다")
        void search() {
            var givenRoomId = 1L;

            var givenCondition = new SearchReviewRequest(2);
            var givenPageable = new PageReviewRequest(10, 10);

            var givenResult = List.of(new SearchReviewResponse(2, "content"));

            when(reviewRepository
                .findRoomReviewByCondition(anyLong(), any(SearchReviewRequest.class), any()))
                .thenReturn(givenResult);

            reviewService.searchRoomReviews(givenRoomId, givenCondition, givenPageable.of());

            then(reviewRepository).should(times(1))
                .findRoomReviewByCondition(givenRoomId, givenCondition, givenPageable.of());
        }

    }

    @Nested
    @DisplayName("게스트는 본인이 작성한 리뷰를 수정할 수 있다 #81")
    class EditReview {
        Review givenReview;
        ReservationReviewResponse givenReservationReviewDto;
        EditReviewRequest givenEditRequest;

        @BeforeEach
        void setAdditionalGiven() {
            givenReview = createReviewWithId(givenReservation);
            givenReservationReviewDto = ReservationReviewResponse.from(givenReservation);

            givenEditRequest = new EditReviewRequest("edit-content", 5);
        }

        @Test
        @DisplayName("리뷰를 수정한다")
        void createUserReview() {
            when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(givenReview));
            when(reservationGuestService.findById(anyLong())).thenReturn(givenReservationReviewDto);

            var guest = givenReservation.getGuest();
            var result = reviewService.editReview(guest.getId(), givenReview.getId(), givenEditRequest);

            then(reviewRepository).should(times(1)).findById(givenReview.getId());
            then(reservationGuestService).should(times(1)).findById(givenReservation.getId());

            assertAll(
                () -> assertThat(result).isInstanceOf(EditReviewResponse.class),
                () -> assertThat(result.getContent()).isEqualTo(givenEditRequest.getContent()),
                () -> assertThat(result.getScore()).isEqualTo(givenEditRequest.getScore())
            );
        }

    }

    @Nested
    @DisplayName("게스트는 본인이 작성한 리뷰를 삭제할 수 있다 #82")
    class DeleteReview {
        Review givenReview;
        ReservationReviewResponse givenReservationDto;

        @BeforeEach
        void setAdditionalGivne() {
            givenReview = createReviewWithId(givenReservation);
            givenReservationDto = ReservationReviewResponse.from(givenReservation);
        }

        @Test
        @DisplayName("리뷰를 삭제한다")
        void deleteUserReview() {
            when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(givenReview));
            when(reservationGuestService.findById(anyLong())).thenReturn(givenReservationDto);

            var guest = givenReservation.getGuest();
            reviewService.deleteReview(guest.getId(), givenReview.getId());

            then(reviewRepository).should(times(1)).findById(givenReview.getId());
            then(reviewRepository).should(times(1)).deleteById(givenReview.getId());
        }
    }
}
