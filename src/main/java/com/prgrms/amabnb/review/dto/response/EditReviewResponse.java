package com.prgrms.amabnb.review.dto.response;

import com.prgrms.amabnb.review.dto.request.EditReviewRequest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EditReviewResponse {
    private int score;
    private String content;

    public static EditReviewResponse from(EditReviewRequest dto) {
        return new EditReviewResponse(dto.getScore(), dto.getContent());
    }
}
