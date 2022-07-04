package com.prgrms.amabnb.reservation.dto.response;

import com.prgrms.amabnb.reservation.entity.Reservation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class ReservationResponseForGuest {

    private ReservationInfoResponse reservation;
    private RoomInfoResponse room;
    private HostInfoResponse host;

    public static ReservationResponseForGuest from(Reservation reservation) {
        return new ReservationResponseForGuest(
            ReservationInfoResponse.from(reservation),
            RoomInfoResponse.from(reservation.getRoom()),
            HostInfoResponse.from(reservation.getRoom().getHost())
        );
    }

}
