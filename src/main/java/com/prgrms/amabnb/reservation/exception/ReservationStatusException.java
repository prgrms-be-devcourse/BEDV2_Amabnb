package com.prgrms.amabnb.reservation.exception;

import com.prgrms.amabnb.common.exception.InvalidValueException;

public class ReservationStatusException extends InvalidValueException {

    private static final String MESSAGE = "변경할 수 없는 예약입니다.";

    public ReservationStatusException() {
        super(MESSAGE);
    }
}
