package com.prgrms.amabnb.reservation.exception;

import org.springframework.http.HttpStatus;

import com.prgrms.amabnb.common.exception.BusinessException;

public class ReservationNotHavePermissionException extends BusinessException {

    public ReservationNotHavePermissionException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }

}
