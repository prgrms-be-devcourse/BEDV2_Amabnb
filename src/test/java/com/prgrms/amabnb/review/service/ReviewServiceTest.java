package com.prgrms.amabnb.review.service;

import com.prgrms.amabnb.common.vo.Email;
import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.reservation.dto.response.ReservationReviewResponse;
import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.ReservationStatus;
import com.prgrms.amabnb.reservation.entity.vo.ReservationDate;
import com.prgrms.amabnb.reservation.service.ReservationGuestService;
import com.prgrms.amabnb.review.dto.request.CreateReviewRequest;
import com.prgrms.amabnb.review.entity.Review;
import com.prgrms.amabnb.review.repository.ReviewRepository;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.entity.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static com.prgrms.amabnb.review.service.ReviewServiceTest.Fixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReservationGuestService reservationGuestService;

    static class Fixture {
        public static User createUser(String name) {
            return User.builder()
                    .id(1L)
                    .name(name)
                    .userRole(UserRole.GUEST)
                    .provider("kakao")
                    .oauthId("oauthId")
                    .email(new Email("kimziou77@naver.com"))
                    .profileImgUrl("something url")
                    .build();

        }

        public static Reservation createReservation(User guest, Room room) {
            return Reservation.builder()
                    .id(1L)
                    .reservationDate(new ReservationDate(LocalDate.now(), LocalDate.now().plusDays(3L)))
                    .totalGuest(1)
                    .totalPrice(new Money(1000))
                    .room(room)
                    .guest(guest)
                    .build();
        }

        public static Room createRoom(User user) {
            var room = Room.builder()
                    .id(1L)
                    .name("방이름")
                    .price(new Money(1000))
                    .description("방설명")
                    .maxGuestNum(10)
                    .host(user)
                    .address(new RoomAddress("12345", "address", "detailAddress"))
                    .roomOption(new RoomOption(1, 1, 1))
                    .roomType(RoomType.HOUSE)
                    .roomScope(RoomScope.PUBLIC)
                    .build();
            return room;
        }
    }

    @Nested
    @DisplayName("게스트는 예약했던 숙소에 리뷰를 작성할 수 있다 #68")
    class CreateReview {
        User givenGuest = createUser("su");
        User givenHost = createUser("bin");
        Reservation givenReservation = createReservation(givenGuest, createRoom(givenHost));

        @Test
        @DisplayName("리뷰를 작성한다")
        void createUserReview() {
            givenReservation.changeStatus(ReservationStatus.COMPLETED);
            var givenReservationDto = ReservationReviewResponse.from(givenReservation);
            var givenReview = new Review(1L, "content", 2, givenReservation);
            var givenRequestDto = new CreateReviewRequest("content", 2);
            when(reservationGuestService.findById(anyLong())).thenReturn(givenReservationDto);
            when(reviewRepository.save(any(Review.class))).thenReturn(givenReview);
            when(reviewRepository.existsByReservationId(anyLong())).thenReturn(false);

            var result = reviewService.createReview(givenGuest.getId(), givenReservation.getId(), givenRequestDto);

            then(reservationGuestService).should(times(1)).findById(anyLong());
            then(reviewRepository).should(times(1)).existsByReservationId(anyLong());
            then(reviewRepository).should(times(1)).save(any(Review.class));
            assertThat(result).isEqualTo(givenReview.getId());
        }
    }

    @Nested
    @DisplayName("게스트는 본인이 작성한 리뷰를 삭제할 수 있다 #82")
    class DeleteReview {
        User givenGuest = createUser("su");
        User givenHost = createUser("bin");
        Reservation givenReservation = createReservation(givenGuest, createRoom(givenHost));

        @Test
        @DisplayName("리뷰를 삭제한다")
        void deleteUserReview() {
            givenReservation.changeStatus(ReservationStatus.COMPLETED);
            var givenReview = new Review(1L, "content", 2, givenReservation);
            var reservationDto = new ReservationReviewResponse(givenReservation.getId(), givenReservation.getReservationStatus(), givenGuest.getId());


            when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(givenReview));
            when(reservationGuestService.findById(anyLong())).thenReturn(reservationDto);

            reviewService.deleteReview(givenGuest.getId(), givenReview.getId());

            then(reviewRepository).should(times(1)).findById(givenReview.getId());
            then(reviewRepository).should(times(1)).deleteByid(givenReview.getId());
        }
    }
}
