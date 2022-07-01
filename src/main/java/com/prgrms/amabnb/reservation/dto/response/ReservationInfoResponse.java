package com.prgrms.amabnb.reservation.dto.response;

import java.time.LocalDate;

import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.ReservationStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class ReservationInfoResponse {

    private Long id;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int totalGuest;
    private int totalPrice;
    private ReservationStatus reservationStatus;

    public static ReservationInfoResponse from(Reservation reservation) {
        return new ReservationInfoResponse(
            reservation.getId(),
            reservation.getReservationDate().getCheckIn(),
            reservation.getReservationDate().getCheckOut(),
            reservation.getTotalGuest(),
            reservation.getTotalPrice().getValue(),
            reservation.getReservationStatus()
        );
    }

}
