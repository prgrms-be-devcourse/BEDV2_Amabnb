package com.prgrms.amabnb.review.exception;

import com.prgrms.amabnb.common.exception.InvalidValueException;

public class ReviewException extends InvalidValueException {
    public ReviewException(String message) {
        super(message);
    }
}
