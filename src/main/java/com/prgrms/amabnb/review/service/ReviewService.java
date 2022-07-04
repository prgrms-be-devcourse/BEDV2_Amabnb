package com.prgrms.amabnb.review.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.ReservationStatus;
import com.prgrms.amabnb.reservation.service.ReservationGuestService;
import com.prgrms.amabnb.review.dto.request.CreateReviewRequest;
import com.prgrms.amabnb.review.entity.Review;
import com.prgrms.amabnb.review.exception.ReviewException;
import com.prgrms.amabnb.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final ReservationGuestService reservationGuestService;

    @Transactional
    public Long createReview(Long userId, Long reservationId, CreateReviewRequest dto) {

        var reservationDto = reservationGuestService.findById(reservationId);

        if (reservationDto.getStatus() != ReservationStatus.COMPLETED) {
            throw new ReviewException("숙소 방문 완료 후 리뷰를 작성할 수 있습니다");
        }

        if(reviewRepository.existsByReservationId(reservationId)){
            throw new ReviewException("이미 작성한 예약 건 입니다 -> reservationID : " + reservationId);
        }

        if (reservationDto.getGuestId() != userId) {
            throw new ReviewException("예약자 본인만 리뷰를 작성할 수 있습니다.");
        }

        var reservation = new Reservation(reservationId);
        var review = new Review(dto.getContent(), dto.getScore(), reservation);
        var savedReview = reviewRepository.save(review);
        return savedReview.getId();
    }

}
