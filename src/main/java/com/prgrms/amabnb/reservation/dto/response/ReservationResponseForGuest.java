package com.prgrms.amabnb.reservation.dto.response;

import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.repository.ReservationDto;
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
    private RoomInfoResponse room;
    private UserInfoResponse host;

    public static ReservationResponseForGuest from(Reservation reservation) {
        return new ReservationResponseForGuest(
            ReservationInfoResponse.from(reservation),
            RoomInfoResponse.from(reservation.getRoom()),
            UserInfoResponse.from(reservation.getRoom().getHost())
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
            new RoomInfoResponse(
                reservationDto.getRoomId(),
                reservationDto.getName(),
                new RoomAddress(reservationDto.getZipcode(), reservationDto.getAddress(),
                    reservationDto.getDetailAddress())
            ),
            new UserInfoResponse(
                reservationDto.getId(),
                reservationDto.getName(),
                reservationDto.getEmail())
        );
    }

}
