package com.prgrms.amabnb.reservation.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.prgrms.amabnb.common.model.BaseEntity;
import com.prgrms.amabnb.reservation.entity.vo.ReservationDate;
import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.review.entity.Review;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.user.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private ReservationDate reservationDate;

    private int maxGuest;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "total_price"))
    private Money totalPrice;

    @Enumerated(value = EnumType.STRING)
    private ReservationStatus reservationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User guest;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Builder
    public Reservation(
        Long id,
        ReservationDate reservationDate,
        int maxGuest,
        Money totalPrice,
        ReservationStatus reservationStatus,
        Room room,
        User guest
    ) {
        this.id = id;
        this.reservationDate = reservationDate;
        this.maxGuest = maxGuest;
        this.totalPrice = totalPrice;
        this.reservationStatus = reservationStatus;
        this.room = room;
        this.guest = guest;
    }

}
