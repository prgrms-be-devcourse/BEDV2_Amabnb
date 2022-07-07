package com.prgrms.amabnb.review.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.prgrms.amabnb.common.exception.InvalidValueException;
import com.prgrms.amabnb.common.model.BaseEntity;
import com.prgrms.amabnb.reservation.entity.Reservation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {
    private static final int MAX_REVIEW_SCORE = 5;
    private static final int MIN_REVIEW_SCORE = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int score;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", unique = true)
    private Reservation reservation;

    public Review(Long id, String content, int score, Reservation reservation) {
        this.id = id;
        setContent(content);
        setScore(score);
        setReservation(reservation);
    }

    public Review(String content, int score, Reservation reservation) {
        this(null, content, score, reservation);
    }

    public void changeContent(String content) {
        setContent(content);
    }

    public void changeScore(int score) {
        setScore(score);
    }

    private void setContent(String content) {
        if (content == null || content.isEmpty() || content.isBlank()) {
            throw new InvalidValueException("리뷰 내용은 비어있을 수 없습니다.");
        }
        this.content = content;
    }

    private void setScore(int score) {
        if (score < MIN_REVIEW_SCORE || score > MAX_REVIEW_SCORE) {
            throw new InvalidValueException("리뷰 평점은 1~5점 사이로 매겨주세요.");
        }
        this.score = score;
    }

    private void setReservation(Reservation reservation) {
        if (reservation == null) {
            throw new InvalidValueException("예약은 비어있을 수 없습니다.");
        }
        this.reservation = reservation;
    }
}
