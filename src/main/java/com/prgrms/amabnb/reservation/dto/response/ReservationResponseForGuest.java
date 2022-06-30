package com.prgrms.amabnb.reservation.dto.response;

import java.time.LocalDate;

import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.ReservationStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class ReservationResponseForGuest {

    private Long id;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int totalGuest;
    private int totalPrice;
    private ReservationStatus reservationStatus;
    private RoomInfoResponse room;
    private HostInfoResponse host;

    public static ReservationResponseForGuest from(Reservation reservation) {
        return ReservationResponseForGuest.builder()
            .id(reservation.getId())
            .checkIn(reservation.getReservationDate().getCheckIn())
            .checkOut(reservation.getReservationDate().getCheckOut())
            .totalGuest(reservation.getTotalGuest())
            .totalPrice(reservation.getTotalPrice().getValue())
            .reservationStatus(reservation.getReservationStatus())
            .room(RoomInfoResponse.from(reservation.getRoom()))
            .host(HostInfoResponse.from(reservation.getRoom().getHost()))
            .build();
    }

}
