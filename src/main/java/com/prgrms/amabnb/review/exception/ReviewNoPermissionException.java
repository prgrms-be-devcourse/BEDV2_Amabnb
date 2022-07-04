package com.prgrms.amabnb.review.exception;

import com.prgrms.amabnb.common.exception.InvalidValueException;

public class ReviewNoPermissionException extends InvalidValueException {
    private static final String MESSAGE = "예약자 본인만 리뷰를 작성할 수 있습니다.";

    public ReviewNoPermissionException() {
        super(MESSAGE);
    }
}
