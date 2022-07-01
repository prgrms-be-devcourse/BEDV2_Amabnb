package com.prgrms.amabnb.reservation.exception;

import com.prgrms.amabnb.common.exception.EntityNotFoundException;

public class ReservationNotFoundException extends EntityNotFoundException {

    private static final String MESSAGE = "존재하지 않는 예약입니다";

    public ReservationNotFoundException() {
        super(MESSAGE);
    }

}
