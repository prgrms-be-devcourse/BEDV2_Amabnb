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
import com.prgrms.amabnb.reservation.exception.ReservationInvalidValueException;
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

    private static final int GUEST_MIN_VALUE = 1;

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
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
        Room room,
        User guest
    ) {
        this.id = id;
        setReservationDate(reservationDate);
        setMaxGuest(maxGuest);
        setTotalPrice(totalPrice);
        setRoom(room);
        setGuest(guest);
        reservationStatus = ReservationStatus.PENDING;
    }

    public void setReservationDate(ReservationDate reservationDate) {
        if (reservationDate == null) {
            throw new ReservationInvalidValueException("예약 날짜는 비어있을 수 없습니다.");
        }
        this.reservationDate = reservationDate;
    }

    private void setMaxGuest(int maxGuest) {
        if (maxGuest < GUEST_MIN_VALUE) {
            throw new ReservationInvalidValueException("최대 인원 수는 0미만일 수 없습니다.");
        }
        this.maxGuest = maxGuest;
    }

    private void setTotalPrice(Money totalPrice) {
        if (totalPrice == null) {
            throw new ReservationInvalidValueException("총 가격은 비어있을 수 없습니다.");
        }
        this.totalPrice = totalPrice;
    }

    private void setRoom(Room room) {
        if (room == null) {
            throw new ReservationInvalidValueException("숙소는 비어있을 수 없습니다.");
        }
        this.room = room;
    }

    private void setGuest(User guest) {
        if (guest == null) {
            throw new ReservationInvalidValueException("게스트는 비어있을 수 없습니다.");
        }
        this.guest = guest;
    }

}
