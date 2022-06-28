package com.prgrms.amabnb.room.repository;

import static com.prgrms.amabnb.room.entity.QRoom.*;
import static com.prgrms.amabnb.room.entity.QRoomImage.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.prgrms.amabnb.room.dto.request.SearchRoomFilterCondition;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QueryRoomRepositoryImpl implements QueryRoomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Room> findRoomsByFilterCondition(SearchRoomFilterCondition filterCondition, Pageable pageable) {

        return jpaQueryFactory.selectFrom(room)
            .leftJoin(room.roomImages, roomImage)
            .fetchJoin()
            .where(applyAllFilters(filterCondition))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    }

    private BooleanExpression applyAllFilters(SearchRoomFilterCondition filterCondition) {
        return Objects.isNull(filterCondition) ? null : bedsGoe(filterCondition.getMinBeds())
            .and(bedroomsGoe(filterCondition.getMinBedrooms()))
            .and(bathroomsGoe(filterCondition.getMinBathrooms()))
            .and(priceGoe(filterCondition.getMinPrice()))
            .and(priceLoe(filterCondition.getMaxPrice()))
            .and(roomTypeEq(filterCondition.getRoomTypes()))
            .and(roomScopesEq(filterCondition.getRoomScopes()));
    }

    private BooleanExpression roomScopesEq(List<String> roomScopes) {
        return Objects.isNull(roomScopes) ? null :
            room.roomScope.in(toRoomScope(roomScopes));
    }

    private BooleanExpression roomTypeEq(List<String> roomTypes) {
        return Objects.isNull(roomTypes) ? null :
            room.roomType.in(toRoomType(roomTypes));
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

    private List<RoomType> toRoomType(List<String> roomTypes) {
        List<RoomType> roomTypeList = new ArrayList<>();
        for (String roomType : roomTypes) {
            roomTypeList.add(RoomType.valueOf(roomType));
        }
        return roomTypeList;
    }

    private List<RoomScope> toRoomScope(List<String> roomScopes) {
        List<RoomScope> roomScopeList = new ArrayList<>();
        for (String roomScope : roomScopes) {
            roomScopeList.add(RoomScope.valueOf(roomScope));
        }
        return roomScopeList;
    }
}
