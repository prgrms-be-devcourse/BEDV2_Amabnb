package com.prgrms.amabnb.review.service;

import com.prgrms.amabnb.review.exception.AlreadyReviewException;
import com.prgrms.amabnb.review.exception.ReviewNoPermissionException;
import com.prgrms.amabnb.review.exception.ReviewNotValidStatusException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.ReservationStatus;
import com.prgrms.amabnb.reservation.service.ReservationGuestService;
import com.prgrms.amabnb.review.dto.request.CreateReviewRequest;
import com.prgrms.amabnb.review.entity.Review;
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
            throw new ReviewNotValidStatusException();
        }

        if(reviewRepository.existsByReservationId(reservationId)){
            throw new AlreadyReviewException();
        }

        if (reservationDto.getGuestId() != userId) {
            throw new ReviewNoPermissionException();
        }

        var reservation = new Reservation(reservationId);
        var review = new Review(dto.getContent(), dto.getScore(), reservation);
        var savedReview = reviewRepository.save(review);
        return savedReview.getId();
    }

}
