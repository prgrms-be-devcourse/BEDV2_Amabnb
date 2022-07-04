package com.prgrms.amabnb.review.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.prgrms.amabnb.common.model.BaseEntity;
import com.prgrms.amabnb.reservation.entity.Reservation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;
    private String content;
    private int score;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", unique = true)
    private Reservation reservation;

    public Review(Long id, String content, int score, Reservation reservation) {
        this.id = id;
        this.content = content;
        this.score = score;
        this.reservation = reservation;
    }

    public Review(String content, int score, Reservation reservation) {
        this(null, content, score, reservation);
    }

}
