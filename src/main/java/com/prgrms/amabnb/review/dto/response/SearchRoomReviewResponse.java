package com.prgrms.amabnb.review.dto.response;

import com.prgrms.amabnb.review.entity.Review;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchRoomReviewResponse {
    String content;
    int score;

    public static SearchRoomReviewResponse from(Review review) {
        return new SearchRoomReviewResponse(review.getContent(), review.getScore());
    }
}
