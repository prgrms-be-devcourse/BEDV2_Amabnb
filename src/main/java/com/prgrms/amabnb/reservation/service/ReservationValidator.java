package com.prgrms.amabnb.reservation.service;

import org.springframework.stereotype.Component;

import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.exception.ReservationInvalidValueException;
import com.prgrms.amabnb.room.entity.Room;

@Component
public class ReservationValidator {

    public void validate(Reservation reservation) {
        validateRoomPrice(reservation);
        validateMaxGuest(reservation);
    }

    private void validateRoomPrice(Reservation reservation) {
        Room room = reservation.getRoom();
        int period = reservation.getReservationDate().getPeriod();
        if (!room.isValidatePrice(reservation.getTotalPrice(), period)) {
            throw new ReservationInvalidValueException("숙소 가격이 일치하지 않습니다.");
        }
    }

    private void validateMaxGuest(Reservation reservation) {
        Room room = reservation.getRoom();
        if (room.isOverMaxGuestNum(reservation.getTotalGuest())) {
            throw new ReservationInvalidValueException("숙소의 최대 인원을 넘을 수 없습니다.");
        }
    }

}
