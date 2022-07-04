package com.prgrms.amabnb.review.exception;

import com.prgrms.amabnb.common.exception.InvalidValueException;

public class ReviewNotValidStatusException extends InvalidValueException {
    private static final String MESSAGE = "숙소 방문 완료 후 리뷰를 작성할 수 있습니다.";

    public ReviewNotValidStatusException() {
        super(MESSAGE);
    }
}
