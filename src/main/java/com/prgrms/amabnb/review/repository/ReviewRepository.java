package com.prgrms.amabnb.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.amabnb.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByReservationId(Long reservationId);
}
