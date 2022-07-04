package com.prgrms.amabnb.review.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.ReservationStatus;
import com.prgrms.amabnb.reservation.service.ReservationGuestService;
import com.prgrms.amabnb.review.dto.request.CreateReviewRequest;
import com.prgrms.amabnb.review.dto.request.EditReviewRequest;
import com.prgrms.amabnb.review.dto.response.EditedReviewResponse;
import com.prgrms.amabnb.review.entity.Review;
import com.prgrms.amabnb.review.exception.ReviewException;
import com.prgrms.amabnb.review.exception.ReviewNotFoundException;
import com.prgrms.amabnb.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final ReservationGuestService reservationGuestService;
    private final ReviewCacheService reviewCacheService;

    @Transactional
    public Long createReview(Long userId, Long reservationId, CreateReviewRequest dto) {

        var reservationDto = reservationGuestService.findById(reservationId);

        if (reservationDto.getStatus() != ReservationStatus.COMPLETED) {
            throw new ReviewException("숙소 방문 완료 후 리뷰를 작성할 수 있습니다");
        }

        // reservationService.findById(reservationId);
        // 굳이 따른 가능성을 만드는 느낌!!! 캐시는 조심해서 써야한다
        if (reviewCacheService.existReservation(reservationId)) {
            throw new ReviewException("이미 작성한 예약 건 입니다 -> reservationID : " + reservationId);
        }
        // 캐싱처리 하는건 정말 변하지 않는 것들을 주로 캐싱하게 된다
        // 카테고리 같은거 이런애들을 하고 변하는애들은 캐싱처리를 안한다!
        // 트래픽이 엄청 많이 나가는 애들을 캐싱처리 함
        // 리뷰는 트래픽이 많이 안나가서 룸같은애들을 처리하긴 함
        // 로컬말고 레디스같은애들한테 저장해놓고 기간만료 3분 2분 1분 이렇게해서 자동으로 삭제되도록
        // 그곳이 트래픽이 제일 많아서 !!
        // 스프링에도 캐시처리하는게 있따
        // 데이터가 달라지면 안된다!
        if (reservationDto.getGuestId() != userId) {
            throw new ReviewException("예약자 본인만 리뷰를 작성할 수 있습니다.");
        }

        var reservation = new Reservation(reservationId);
        var review = new Review(dto.getContent(), dto.getScore(), reservation);
        var savedReview = reviewRepository.save(review);
        reviewCacheService.addReview(reservationId);
        return savedReview.getId();
    }

    public EditedReviewResponse editReview(Long userId, Long reviewId, EditReviewRequest editedReview) {
        reviewRepository.findById(reviewId) // 쿼리1 : review & reservation fetch join
            .orElseThrow(ReviewNotFoundException::new);

        // 쿼리2 : select r.guestId from Reservation r where id=:id -> guestId

        // userId == guestId (예약자 본인만 변경 가능)

        return null;
    }
}
