package com.prgrms.amabnb.reservation.dto.response;

import com.prgrms.amabnb.reservation.entity.Reservation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ReservationResponseForHost {

    private ReservationInfoResponse reservation;
    private RoomInfoResponse room;
    private UserInfoResponse guest;

    public static ReservationResponseForHost from(Reservation reservation) {
        return new ReservationResponseForHost(
            ReservationInfoResponse.from(reservation),
            RoomInfoResponse.from(reservation.getRoom()),
            UserInfoResponse.from(reservation.getGuest())
        );
    }

}
