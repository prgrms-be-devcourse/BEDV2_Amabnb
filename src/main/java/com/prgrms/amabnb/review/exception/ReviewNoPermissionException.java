package com.prgrms.amabnb.review.exception;

import com.prgrms.amabnb.common.exception.InvalidValueException;

public class ReviewNoPermissionException extends InvalidValueException {
    private static final String MESSAGE = "리뷰에 대한 권한이 존재하지 않습니다.";

    public ReviewNoPermissionException() {
        super(MESSAGE);
    }
}
