package com.prgrms.amabnb.reservation.repository;

import static com.prgrms.amabnb.reservation.entity.QReservation.*;
import static com.prgrms.amabnb.room.entity.QRoom.*;
import static com.prgrms.amabnb.user.entity.QUser.*;

import java.time.LocalDate;
import java.util.List;

import com.prgrms.amabnb.reservation.dto.response.ReservationDateResponse;
import com.prgrms.amabnb.reservation.dto.response.ReservationDto;
import com.prgrms.amabnb.reservation.entity.ReservationStatus;
import com.prgrms.amabnb.reservation.entity.vo.ReservationDate;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.user.entity.User;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existReservationByRoom(Room room, Long reservationId, ReservationDate reservationDate) {
        LocalDate checkIn = reservationDate.getCheckIn();
        LocalDate checkOut = reservationDate.getCheckOut();

        Integer fetchOne = queryFactory.selectOne()
            .from(reservation)
            .where(
                eqRoom(room)
                , notEqReservationId(reservationId)
                , notInCanceled()
                , betweenCheckIn(checkIn, checkOut).or(betweenCheckOut(checkIn, checkOut))
            )
            .fetchFirst();

        return fetchOne != null;
    }

    @Override
    public List<ReservationDateResponse> findReservationDates(
        Long roomId,
        LocalDate startDate,
        LocalDate endDate
    ) {
        return queryFactory.select(Projections.constructor(ReservationDateResponse.class,
                reservation.reservationDate.checkIn,
                reservation.reservationDate.checkOut))
            .from(reservation)
            .where(reservation.room.id.eq(roomId),
                notInCanceled(),
                reservation.reservationDate.checkIn.between(startDate, endDate))
            .fetch();
    }

    @Override
    public List<ReservationDto> findReservationsByGuestAndStatus(
        Long lastReservationId,
        int pageSize,
        User guest,
        ReservationStatus status
    ) {
        return queryFactory.select(toReservationDto(status))
            .from(reservation)
            .innerJoin(reservation.room, room)
            .innerJoin(room.host, user)
            .where(ltReservationId(lastReservationId),
                eqStatus(status),
                eqGuest(guest))
            .limit(pageSize)
            .orderBy(reservation.id.desc())
            .fetch();
    }

    @Override
    public List<ReservationDto> findReservationsByHostAndStatus(
        Long reservationId,
        int pageSize,
        User host,
        ReservationStatus status
    ) {
        return queryFactory.select(toReservationDto(status))
            .from(reservation)
            .innerJoin(reservation.room, room)
            .innerJoin(reservation.guest, user)
            .where(ltReservationId(reservationId),
                eqStatus(status),
                eqHost(host))
            .limit(pageSize)
            .orderBy(reservation.id.desc())
            .fetch();
    }

    private QBean<ReservationDto> toReservationDto(ReservationStatus status) {
        return Projections.fields(ReservationDto.class,
            reservation.id,
            reservation.reservationDate.checkIn,
            reservation.reservationDate.checkOut,
            reservation.totalGuest,
            reservation.totalPrice.value.as("totalPrice"),
            statusExpression(status),
            reservation.room.id.as("roomId"),
            reservation.room.name.as("roomName"),
            reservation.room.address.zipcode,
            reservation.room.address.address,
            reservation.room.address.detailAddress,
            user.id.as("userId"),
            user.name,
            user.email.value.as("email"));
    }

    private BooleanExpression notInCanceled() {
        return reservation.reservationStatus.notIn(ReservationStatus.HOST_CANCELED, ReservationStatus.GUEST_CANCELED);
    }

    private BooleanExpression betweenCheckOut(LocalDate checkIn, LocalDate checkOut) {
        return reservation.reservationDate.checkOut.between(checkIn.plusDays(1L), checkOut);
    }

    private BooleanExpression betweenCheckIn(LocalDate checkIn, LocalDate checkOut) {
        return reservation.reservationDate.checkIn.between(checkIn, checkOut.minusDays(1L));
    }

    private BooleanExpression eqRoom(Room room) {
        return reservation.room.eq(room);
    }

    private BooleanExpression eqGuest(User guest) {
        return reservation.guest.eq(guest);
    }

    private BooleanExpression eqHost(User host) {
        return reservation.room.host.eq(host);
    }

    private BooleanExpression ltReservationId(Long reservationId) {
        if (reservationId == null) {
            return null;
        }
        return reservation.id.lt(reservationId);

    }

    private BooleanExpression eqStatus(ReservationStatus reservationStatus) {
        if (reservationStatus == null) {
            return null;
        }
        return reservation.reservationStatus.eq(reservationStatus);
    }

    private EnumExpression<ReservationStatus> statusExpression(ReservationStatus status) {
        if (status == null) {
            return Expressions.asEnum(reservation.reservationStatus);
        }

        return Expressions.asEnum(status).as(reservation.reservationStatus);
    }

    private BooleanExpression notEqReservationId(Long id) {
        if (id == null) {
            return null;
        }
        return reservation.id.ne(id);
    }

}
