package com.prgrms.amabnb.reservation.exception;

import com.prgrms.amabnb.common.exception.InvalidValueException;

public class ReservationInvalidValueException extends InvalidValueException {

    public ReservationInvalidValueException(String message) {
        super(message);
    }
}
