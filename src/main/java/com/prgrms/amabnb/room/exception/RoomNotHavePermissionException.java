package com.prgrms.amabnb.room.exception;

import org.springframework.http.HttpStatus;

import com.prgrms.amabnb.common.exception.BusinessException;

public class RoomNotHavePermissionException extends BusinessException {

    public RoomNotHavePermissionException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
