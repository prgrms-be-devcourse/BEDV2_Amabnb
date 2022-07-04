package com.prgrms.amabnb.reservation.dto.response;

import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.ReservationStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationReviewResponse {
    private Long reservationId;
    private ReservationStatus status;
    private Long guestId;

    public ReservationReviewResponse(Long reservationId, ReservationStatus status, Long guestId) {
        this.reservationId = reservationId;
        this.status = status;
        this.guestId = guestId;
    }

    public static ReservationReviewResponse from(Reservation reservation) {
        return new ReservationReviewResponse(
                reservation.getId(),
                reservation.getReservationStatus(),
                reservation.getGuest().getId()
        );
    }
}
