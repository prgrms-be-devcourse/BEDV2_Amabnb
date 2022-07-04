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
        // userId로 찾아와서
        cache = reviewRepository.findAll().stream()
            .map(Review::getReservation)
            .map(Reservation::getId)
            .collect(Collectors.toSet());
    }
    // 인스턴스가 여러개일때 Repository에서 가져온 값들이 다 달라진다. 새로운 데이터가 추가될 수 있어서 안맞을수있다
    // 2시간주기로 refresh 해오면

    public boolean existReservation(Long reservationId) {
        return cache.contains(reservationId);
    }

    public void addReview(Long reservationId) {
        cache.add(reservationId);
    }
}
