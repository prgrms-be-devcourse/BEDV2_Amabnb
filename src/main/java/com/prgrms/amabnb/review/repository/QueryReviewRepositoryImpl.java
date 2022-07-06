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
    public List<SearchReviewResponse> findMyReviewByCondition(
        Long userId, SearchReviewRequest condition, Pageable pageable) {
        return jpaQueryFactory.select(
                Projections.constructor(SearchReviewResponse.class, review.score, review.content))
            .from(review)
            .innerJoin(review.reservation)
            .innerJoin(review.reservation.guest)
            .on(review.reservation.guest.id.eq(userId))
            .where(
                scoreEq(condition.getScore())
            )
            .orderBy(review.id.asc(), review.createdAt.desc(), review.score.desc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();
    }

    @Override
    public List<SearchReviewResponse> findRoomReviewByCondition(
        Long roomId, SearchReviewRequest condition, Pageable pageable) {
        return jpaQueryFactory.select(
                Projections.constructor(SearchReviewResponse.class, review.score, review.content))
            .from(review)
            .innerJoin(review.reservation)
            .innerJoin(review.reservation.room)
            .on(review.reservation.room.id.eq(roomId))
            .where(
                scoreEq(condition.getScore())
            )
            .orderBy(review.id.asc(), review.createdAt.desc(), review.score.desc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();
    }

    private BooleanExpression scoreEq(Integer score) {
        return Objects.isNull(score) ? null : review.score.eq(score);
    }

}
