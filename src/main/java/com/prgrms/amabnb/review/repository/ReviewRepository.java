package com.prgrms.amabnb.review.repository;

import com.prgrms.amabnb.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByReservationId(Long reservationId);

    void deleteByid(Long reviewId);
}
