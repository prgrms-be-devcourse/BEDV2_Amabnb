package com.prgrms.amabnb.review.repository;

import static com.prgrms.amabnb.config.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.amabnb.config.RepositoryTest;
import com.prgrms.amabnb.reservation.repository.ReservationRepository;
import com.prgrms.amabnb.review.dto.request.PageReviewRequest;
import com.prgrms.amabnb.review.dto.request.SearchMyReviewRequest;
import com.prgrms.amabnb.review.dto.request.SearchRoomReviewRequest;
import com.prgrms.amabnb.review.dto.response.SearchMyReviewResponse;
import com.prgrms.amabnb.review.dto.response.SearchRoomReviewResponse;
import com.prgrms.amabnb.review.entity.Review;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.repository.RoomRepository;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.repository.UserRepository;

class ReviewRepositoryTest extends RepositoryTest {

    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    ReservationRepository reservationRepository;

    @Nested
    @DisplayName("게스트는 본인이 작성한 리뷰 목록을 조회할 수 있다 #70")
    class FindMyReviewByCondition {
        int givenReviewScore = 2;
        PageReviewRequest pageable = new PageReviewRequest(10, 10);
        SearchMyReviewRequest givenSearchRequest = new SearchMyReviewRequest(givenReviewScore);

        @Test
        @DisplayName("검색 조건에 해당하더라도, 본인이 작성한 글만 조회할 수 있다")
        void findMyReview() {
            var user1 = createUser("user1");
            var user2 = createUser("user2");

            var review1 = SearchMyReviewResponse.from(createReview(user1));
            var review2 = SearchMyReviewResponse.from(createReview(user1));
            var review3 = SearchMyReviewResponse.from(createReview(user2));
            assertThat(reviewRepository.count()).isEqualTo(3);

            var result = reviewRepository.findMyReviewByCondition(user1.getId(), givenSearchRequest, pageable.of());
            assertThat(result.size()).isEqualTo(2);
            assertThat(result).usingRecursiveFieldByFieldElementComparator().contains(review1, review2);
        }

        private Review createReview(User user) {
            var guest = userRepository.save(user);
            var host = userRepository.save(createUser(UUID.randomUUID().toString().substring(20)));
            var room = roomRepository.save(createRoom(host));
            var reservation = reservationRepository.save(createReservation(room, guest));

            return reviewRepository.save(new Review("content", givenReviewScore, reservation));
        }
    }

    @Nested
    @DisplayName("게스트는 숙소의 리뷰를 조회할 수 있다 #67")
    class FindRoomReviewByCondition {
        int givenReviewScore = 2;
        PageReviewRequest pageable = new PageReviewRequest(10, 10);
        SearchRoomReviewRequest condition = new SearchRoomReviewRequest(givenReviewScore);

        @Test
        @DisplayName("선택한 숙소에 대한 리뷰를 조건에 맞게 조회할 수 있다.")
        void findRoomReview() {
            var host = userRepository.save(createUser(UUID.randomUUID().toString().substring(20)));
            var room1 = roomRepository.save(createRoom(host));
            var review1 = SearchRoomReviewResponse.from(writeReview(room1));
            var review2 = SearchRoomReviewResponse.from(writeReview(room1));
            var review3 = SearchRoomReviewResponse.from(writeReview(room1));

            var room2 = roomRepository.save(createRoom(host));
            writeReview(room2);
            writeReview(room2);

            var result = reviewRepository.findRoomReviewByCondition(room1.getId(), condition, pageable.of());
            assertThat(result.size()).isEqualTo(3);
            assertThat(result).usingRecursiveFieldByFieldElementComparator().contains(review1, review2, review3);
        }

        private Review writeReview(Room room) {
            var guest = userRepository.save(createUser(UUID.randomUUID().toString().substring(20)));
            var reservation = reservationRepository.save(createReservation(room, guest));
            return reviewRepository.save(new Review("content", givenReviewScore, reservation));
        }
    }

}
