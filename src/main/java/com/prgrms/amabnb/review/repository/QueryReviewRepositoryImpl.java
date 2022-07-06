package com.prgrms.amabnb.review.repository;

import static com.prgrms.amabnb.review.entity.QReview.*;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.prgrms.amabnb.review.dto.request.SearchReviewRequest;
import com.prgrms.amabnb.review.dto.response.SearchReviewResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QueryReviewRepositoryImpl implements QueryReviewRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<SearchReviewResponse> findAllByCondition(Long userId, SearchReviewRequest condition,
        Pageable pageable) {
        return jpaQueryFactory.select(Projections.constructor(SearchReviewResponse.class, review.score))
            .from(review)
            .where(
                scoreEq(condition.getScore()),
                hasReviewPermission(userId)
            )
            .orderBy(review.score.desc())
            .limit(pageable.getPageSize())
            .fetch();
    }

    private BooleanExpression scoreEq(Integer score) {
        return Objects.isNull(score) ? null : review.score.eq(score);
    }

    private BooleanExpression hasReviewPermission(Long userId) {
        return Objects.isNull(userId) ? null : review.reservation.guest.id.eq(userId);
    }
}
