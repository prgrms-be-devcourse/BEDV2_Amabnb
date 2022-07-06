package com.prgrms.amabnb.review.dto.response;

import com.prgrms.amabnb.review.entity.Review;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchMyReviewResponse {
    private int score;

    public static SearchMyReviewResponse from(Review review) {
        return new SearchMyReviewResponse(review.getScore());
    }
}
