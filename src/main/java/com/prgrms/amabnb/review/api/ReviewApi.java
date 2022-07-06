package com.prgrms.amabnb.review.api;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.amabnb.common.model.ApiResponse;
import com.prgrms.amabnb.review.dto.request.CreateReviewRequest;
import com.prgrms.amabnb.review.dto.request.EditReviewRequest;
import com.prgrms.amabnb.review.dto.request.PageReviewRequest;
import com.prgrms.amabnb.review.dto.request.SearchMyReviewRequest;
import com.prgrms.amabnb.review.dto.request.SearchRoomReviewRequest;
import com.prgrms.amabnb.review.dto.response.EditReviewResponse;
import com.prgrms.amabnb.review.dto.response.SearchMyReviewResponse;
import com.prgrms.amabnb.review.dto.response.SearchRoomReviewResponse;
import com.prgrms.amabnb.review.service.ReviewService;
import com.prgrms.amabnb.security.jwt.JwtAuthentication;

import lombok.RequiredArgsConstructor;

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

    @GetMapping("/rooms/{roomId}/reviews")
    public ResponseEntity<ApiResponse<List<SearchRoomReviewResponse>>> searchRoomReviews(
        @PathVariable Long roomId,
        SearchRoomReviewRequest searchDto,
        PageReviewRequest pageReviewRequest
    ) {
        return ResponseEntity.ok(
            new ApiResponse<>(reviewService.searchRoomReviews(roomId, searchDto, pageReviewRequest.of())));
    }

    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<List<SearchMyReviewResponse>>> searchMyReviews(
        @AuthenticationPrincipal JwtAuthentication user,
        SearchMyReviewRequest searchReviewDto,
        PageReviewRequest pageReviewRequest
    ) {
        return ResponseEntity.ok(
            new ApiResponse<>(reviewService.searchMyReviews(user.id(), searchReviewDto, pageReviewRequest.of())));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
        @AuthenticationPrincipal JwtAuthentication user,
        @PathVariable Long reviewId
    ) {
        reviewService.deleteReview(user.id(), reviewId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<EditReviewResponse>> editReview(
        @AuthenticationPrincipal JwtAuthentication user,
        @PathVariable Long reviewId,
        @Valid @RequestBody EditReviewRequest editDto
    ) {
        var editedReview = reviewService.editReview(user.id(), reviewId, editDto);
        return ResponseEntity.ok().body(new ApiResponse<>(editedReview));
    }

}
