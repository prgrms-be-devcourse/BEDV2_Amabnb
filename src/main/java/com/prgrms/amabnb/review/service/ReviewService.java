package com.prgrms.amabnb.review.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.reservation.dto.response.ReservationReviewResponse;
import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.ReservationStatus;
import com.prgrms.amabnb.reservation.service.ReservationGuestService;
import com.prgrms.amabnb.review.dto.request.CreateReviewRequest;
import com.prgrms.amabnb.review.dto.request.EditReviewRequest;
import com.prgrms.amabnb.review.dto.request.SearchReviewRequest;
import com.prgrms.amabnb.review.dto.response.EditReviewResponse;
import com.prgrms.amabnb.review.dto.response.SearchReviewResponse;
import com.prgrms.amabnb.review.entity.Review;
import com.prgrms.amabnb.review.exception.AlreadyReviewException;
import com.prgrms.amabnb.review.exception.ReviewNoPermissionException;
import com.prgrms.amabnb.review.exception.ReviewNotFoundException;
import com.prgrms.amabnb.review.exception.ReviewNotValidStatusException;
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
        validateReservationStatus(reservationDto);
        validateOneReservationOneReview(reservationId);
        validateUserPermission(userId, reservationDto.getGuestId());

        var reservation = new Reservation(reservationId);
        var review = new Review(dto.getContent(), dto.getScore(), reservation);
        var savedReview = reviewRepository.save(review);
        return savedReview.getId();
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        var review = reviewRepository.findById(reviewId)
            .orElseThrow(ReviewNotFoundException::new);

        var reservationDto = reservationGuestService.findById(review.getReservation().getId());
        validateUserPermission(userId, reservationDto.getGuestId());

        reviewRepository.deleteById(reviewId);
    }

    @Transactional
    public EditReviewResponse editReview(Long userId, Long reviewId, EditReviewRequest editDto) {
        var review = reviewRepository.findById(reviewId)
            .orElseThrow(ReviewNotFoundException::new);

        var reservationDto = reservationGuestService.findById(review.getReservation().getId());
        validateUserPermission(userId, reservationDto.getGuestId());

        review.changeContent(editDto.getContent());
        review.changeScore(editDto.getScore());

        return EditReviewResponse.from(editDto);
    }

    public List<SearchReviewResponse> searchMyReviews(Long userId, SearchReviewRequest searchReviewDto,
        PageRequest pageable) {
        return reviewRepository.findAllByCondition(userId, searchReviewDto, pageable);
    }

    private void validateOneReservationOneReview(Long reservationId) {
        if (reviewRepository.existsByReservationId(reservationId)) {
            throw new AlreadyReviewException();
        }
    }

    private void validateReservationStatus(ReservationReviewResponse dto) {
        if (dto.getStatus() != ReservationStatus.COMPLETED) {
            throw new ReviewNotValidStatusException();
        }
    }

    private void validateUserPermission(Long guestId, Long userId) {
        if (!userId.equals(guestId)) {
            throw new ReviewNoPermissionException();
        }
    }
}
