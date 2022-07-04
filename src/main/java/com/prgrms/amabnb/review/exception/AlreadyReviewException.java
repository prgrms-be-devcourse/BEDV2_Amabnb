package com.prgrms.amabnb.review.exception;

import com.prgrms.amabnb.common.exception.InvalidValueException;

public class AlreadyReviewException extends InvalidValueException {

    private static final String MESSAGE = "이미 작성한 예약 건 입니다.";

    public AlreadyReviewException() {
        super(MESSAGE);
    }
}
