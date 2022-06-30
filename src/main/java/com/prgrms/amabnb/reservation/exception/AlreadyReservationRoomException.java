package com.prgrms.amabnb.reservation.exception;

import com.prgrms.amabnb.common.exception.InvalidValueException;

public class AlreadyReservationRoomException extends InvalidValueException {

    private static final String MESSAGE = "해당 숙소가 이미 예약된 기간입니다.";

    public AlreadyReservationRoomException() {
        super(MESSAGE);
    }

}
