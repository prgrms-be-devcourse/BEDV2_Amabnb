package com.prgrms.amabnb.reservation.exception;

import com.prgrms.amabnb.common.exception.InvalidValueException;

public class AlreadyReservationUserException extends InvalidValueException {

    private static final String MESSAGE = "귀하가 이미 숙소를 예약한 기간입니다.";

    public AlreadyReservationUserException() {
        super(MESSAGE);
    }

}
