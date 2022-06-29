package com.prgrms.amabnb.user.exception;

import com.prgrms.amabnb.common.exception.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {

    private static final String MESSAGE = "존재하지 않는 유저입니다";

    public UserNotFoundException() {
        super(MESSAGE);
    }

}
