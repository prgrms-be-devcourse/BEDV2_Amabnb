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

    public static ReservationResponseForHost from(ReservationDto reservationDto) {
        return new ReservationResponseForHost(
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
