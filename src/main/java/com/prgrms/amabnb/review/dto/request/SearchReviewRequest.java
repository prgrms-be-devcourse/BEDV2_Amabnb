package com.prgrms.amabnb.review.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchReviewRequest {
    private Integer score;

    public SearchReviewRequest(Integer score) {
        this.score = score;
    }
}
