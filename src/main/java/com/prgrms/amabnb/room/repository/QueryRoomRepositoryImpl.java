package com.prgrms.amabnb.room.repository;

import static com.prgrms.amabnb.room.entity.QRoom.*;
import static com.prgrms.amabnb.room.entity.QRoomImage.*;
import static com.querydsl.core.group.GroupBy.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.prgrms.amabnb.room.dto.request.SearchRoomFilterCondition;
import com.prgrms.amabnb.room.dto.response.RoomScrollResponse;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QueryRoomRepositoryImpl implements QueryRoomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<RoomScrollResponse> findRoomsByFilterCondition(SearchRoomFilterCondition filterCondition,
        Pageable pageable) {

        return jpaQueryFactory.from(room)
            .innerJoin(roomImage)
            .on(room.id.eq(roomImage.room.id))
            .where(
                bedsGoe(filterCondition.getMinBeds()),
                bedroomsGoe(filterCondition.getMinBedrooms()),
                bathroomsGoe(filterCondition.getMinBathrooms()),
                priceGoe(filterCondition.getMinPrice()),
                priceLoe(filterCondition.getMaxPrice()),
                roomTypeEq(filterCondition.getRoomTypes()),
                roomScopesEq(filterCondition.getRoomScopes())
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .transform(
                groupBy(room.id).list(
                    Projections.fields(RoomScrollResponse.class,
                        room.id,
                        room.name,
                        room.price.value.as("price"),
                        list(roomImage.imagePath).as("imagePaths")
                    )
                )
            );

    }

    @Override
    public List<Room> findRoomsByHostId(Long userId) {
        return jpaQueryFactory.selectFrom(room)
            .leftJoin(room.roomImages, roomImage)
            .fetchJoin()
            .where(room.host.id.eq(userId))
            .distinct()
            .fetch();
    }

    @Override
    public Optional<Room> findRoomByIdAndHostId(Long roomId, Long hostId) {
        return Optional.ofNullable(jpaQueryFactory
            .selectFrom(room)
            .leftJoin(room.roomImages, roomImage)
            .fetchJoin()
            .where(
                room.id.eq(roomId),
                room.host.id.eq(hostId)
            )
            .fetchOne());
    }

    @Override
    public Optional<Room> findRoomById(Long roomId) {
        return Optional.ofNullable(jpaQueryFactory
            .selectFrom(room)
            .leftJoin(room.roomImages, roomImage)
            .fetchJoin()
            .where(
                room.id.eq(roomId)
            )
            .fetchOne());
    }

    private BooleanExpression roomScopesEq(List<RoomScope> roomScopes) {
        return Objects.isNull(roomScopes) ? null : room.roomScope.in(roomScopes);
    }

    private BooleanExpression roomTypeEq(List<RoomType> roomTypes) {
        return Objects.isNull(roomTypes) ? null : room.roomType.in(roomTypes);
    }

    private BooleanExpression priceLoe(Integer maxPrice) {
        return Objects.isNull(maxPrice) ? null : room.price.value.loe(maxPrice);
    }

    private BooleanExpression priceGoe(Integer minPrice) {
        return Objects.isNull(minPrice) ? null : room.price.value.goe(minPrice);
    }

    private BooleanExpression bathroomsGoe(Integer minBathrooms) {
        return Objects.isNull(minBathrooms) ? null : room.roomOption.bathRoomCnt.goe(minBathrooms);
    }

    private BooleanExpression bedroomsGoe(Integer minBedrooms) {
        return Objects.isNull(minBedrooms) ? null : room.roomOption.bathRoomCnt.goe(minBedrooms);
    }

    private BooleanExpression bedsGoe(Integer minBeds) {
        return Objects.isNull(minBeds) ? null : room.roomOption.bedCnt.goe(minBeds);
    }

}
