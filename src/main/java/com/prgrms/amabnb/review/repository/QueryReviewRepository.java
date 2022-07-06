package com.prgrms.amabnb.review.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.prgrms.amabnb.review.dto.request.SearchMyReviewRequest;
import com.prgrms.amabnb.review.dto.request.SearchRoomReviewRequest;
import com.prgrms.amabnb.review.dto.response.SearchMyReviewResponse;
import com.prgrms.amabnb.review.dto.response.SearchRoomReviewResponse;

public interface QueryReviewRepository {
    List<SearchMyReviewResponse> findMyReviewByCondition(
        Long userId, SearchMyReviewRequest condition, Pageable pageable);

    List<SearchRoomReviewResponse> findRoomReviewByCondition(
        Long roomId, SearchRoomReviewRequest condition, PageRequest pageable);
}
