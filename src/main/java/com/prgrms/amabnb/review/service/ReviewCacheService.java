package com.prgrms.amabnb.review.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.review.entity.Review;
import com.prgrms.amabnb.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewCacheService {
    private final ReviewRepository reviewRepository;
    private Set<Long> cache = new HashSet<>();

    @PostConstruct
    public void postConstruct() {
        cache = reviewRepository.findAll().stream()
            .map(Review::getReservation)
            .map(Reservation::getId)
            .collect(Collectors.toSet());
    }

    public boolean existReservation(Long reservationId) {
        return cache.contains(reservationId);
    }

    public void addReview(Long reservationId) {
        cache.add(reservationId);
    }
}
