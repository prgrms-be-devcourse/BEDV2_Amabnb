package com.prgrms.amabnb.review.repository;

import static com.prgrms.amabnb.review.entity.QReview.*;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.prgrms.amabnb.review.dto.request.SearchMyReviewRequest;
import com.prgrms.amabnb.review.dto.request.SearchRoomReviewRequest;
import com.prgrms.amabnb.review.dto.response.SearchMyReviewResponse;
import com.prgrms.amabnb.review.dto.response.SearchRoomReviewResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QueryReviewRepositoryImpl implements QueryReviewRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<SearchMyReviewResponse> findMyReviewByCondition(Long userId, SearchMyReviewRequest condition,
        Pageable pageable) {
        return jpaQueryFactory.select(Projections.constructor(SearchMyReviewResponse.class, review.score))
            .from(review)
            .innerJoin(review.reservation)
            .innerJoin(review.reservation.guest)
            .on(review.reservation.guest.id.eq(userId))
            .where(
                scoreEq(condition.getScore())
            )
            .orderBy(review.score.desc())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public List<SearchRoomReviewResponse> findRoomReviewByCondition(Long roomId, SearchRoomReviewRequest condition,
        PageRequest pageable) {
        return jpaQueryFactory.select(
                Projections.constructor(SearchRoomReviewResponse.class, review.content, review.score))
            .from(review)
            .innerJoin(review.reservation)
            .innerJoin(review.reservation.room)
            .on(review.reservation.room.id.eq(roomId))
            .where(
                scoreEq(condition.getScore())
            )
            .orderBy(review.score.desc())
            .limit(pageable.getPageSize())
            .fetch();
    }

    private BooleanExpression scoreEq(Integer score) {
        return Objects.isNull(score) ? null : review.score.eq(score);
    }

}
