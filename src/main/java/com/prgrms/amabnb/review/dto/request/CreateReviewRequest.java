package com.prgrms.amabnb.review.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateReviewRequest {
    @NotBlank
    String content;

    @NotNull
    @Range(min = 1, max = 5)
    int score;

}
