package com.prgrms.amabnb.reservation.dto.response;

import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ReservationResponseForGuest {

    private ReservationInfoResponse reservation;
    private ReservationRoomInfoResponse room;
    private ReservationUserInfoResponse host;

    public static ReservationResponseForGuest from(Reservation reservation) {
        return new ReservationResponseForGuest(
            ReservationInfoResponse.from(reservation),
            ReservationRoomInfoResponse.from(reservation.getRoom()),
            ReservationUserInfoResponse.from(reservation.getRoom().getHost())
        );
    }

    public static ReservationResponseForGuest from(ReservationDto reservationDto) {
        return new ReservationResponseForGuest(
            new ReservationInfoResponse(
                reservationDto.getId(),
                reservationDto.getCheckIn(),
                reservationDto.getCheckOut(),
                reservationDto.getTotalGuest(),
                reservationDto.getTotalPrice(),
                reservationDto.getReservationStatus()
            ),
            new ReservationRoomInfoResponse(
                reservationDto.getRoomId(),
                reservationDto.getName(),
                new RoomAddress(
                    reservationDto.getZipcode(),
                    reservationDto.getAddress(),
                    reservationDto.getDetailAddress()
                )
            ),
            new ReservationUserInfoResponse(
                reservationDto.getId(),
                reservationDto.getName(),
                reservationDto.getEmail())
        );
    }

}
