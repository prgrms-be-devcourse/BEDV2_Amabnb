package com.prgrms.amabnb.review.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.prgrms.amabnb.review.dto.request.SearchReviewRequest;
import com.prgrms.amabnb.review.dto.response.SearchReviewResponse;

public interface QueryReviewRepository {
    List<SearchReviewResponse> findMyReviewByCondition(
        Long userId, SearchReviewRequest condition, Pageable pageable);

    List<SearchReviewResponse> findRoomReviewByCondition(
        Long roomId, SearchReviewRequest condition, Pageable pageable);
}
