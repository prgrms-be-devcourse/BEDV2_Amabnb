package com.prgrms.amabnb.review.exception;

import com.prgrms.amabnb.common.exception.EntityNotFoundException;

public class ReviewNotFoundException extends EntityNotFoundException {
    private static final String MESSAGE = "존재하지 않는 리뷰입니다";

    public ReviewNotFoundException() {
        super(MESSAGE);
    }
}
