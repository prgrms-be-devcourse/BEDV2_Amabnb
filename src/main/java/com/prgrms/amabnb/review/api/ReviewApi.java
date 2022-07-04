package com.prgrms.amabnb.review.api;

import com.prgrms.amabnb.review.dto.request.CreateReviewRequest;
import com.prgrms.amabnb.review.service.ReviewService;
import com.prgrms.amabnb.security.jwt.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class ReviewApi {

    private final ReviewService reviewService;

    @PostMapping("/reservations/{reservationId}/reviews")
    public ResponseEntity<Void> createReview(
            @AuthenticationPrincipal JwtAuthentication user,
            @PathVariable Long reservationId,
            @Valid @RequestBody CreateReviewRequest review
    ) {
        var createdReviewId = reviewService.createReview(user.id(), reservationId, review);
        return ResponseEntity.created(URI.create("/reviews/" + createdReviewId)).build();
    }

}
