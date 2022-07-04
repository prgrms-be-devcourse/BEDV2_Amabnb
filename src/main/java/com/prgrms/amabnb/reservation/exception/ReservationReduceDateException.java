package com.prgrms.amabnb.reservation.exception;

import com.prgrms.amabnb.common.exception.InvalidValueException;

public class ReservationReduceDateException extends InvalidValueException {

    private static final String MESSAGE = "예약 변경은 예약 연장시에만 가능합니다.";

    public ReservationReduceDateException() {
        super(MESSAGE);
    }

}
