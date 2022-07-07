package com.prgrms.amabnb.review.dto.request;

import org.hibernate.validator.constraints.Range;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchReviewRequest {
    @Range(min = 1, max = 5)
    private Integer score;
}
