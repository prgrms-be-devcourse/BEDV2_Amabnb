package com.prgrms.amabnb.room.exception;

import com.prgrms.amabnb.common.exception.EntityNotFoundException;

public class RoomNotFoundException extends EntityNotFoundException {

    private static final String MESSAGE = "존재하지 않는 숙소입니다";

    public RoomNotFoundException() {
        super(MESSAGE);
    }
}
