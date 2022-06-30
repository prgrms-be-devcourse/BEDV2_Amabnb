package com.prgrms.amabnb.reservation.repository;

import static com.prgrms.amabnb.reservation.entity.QReservation.*;

import java.time.LocalDate;

import com.prgrms.amabnb.reservation.entity.ReservationStatus;
import com.prgrms.amabnb.reservation.entity.vo.ReservationDate;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.user.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existReservation(Room room, ReservationDate reservationDate) {
        LocalDate checkIn = reservationDate.getCheckIn();
        LocalDate checkOut = reservationDate.getCheckOut();

        Integer fetchOne = queryFactory.selectOne()
            .from(reservation)
            .where(
                eqRoom(room)
                    .and(notInCanceled())
                    .and(betweenCheckIn(checkIn, checkOut).or(betweenCheckOut(checkIn, checkOut)))
            )
            .fetchFirst();

        return fetchOne != null;
    }

    @Override
    public boolean existReservationByGuest(User guest, ReservationDate reservationDate) {
        LocalDate checkIn = reservationDate.getCheckIn();
        LocalDate checkOut = reservationDate.getCheckOut();

        Integer fetchOne = queryFactory.selectOne()
            .from(reservation)
            .where(
                eqGuest(guest)
                    .and(notInCanceled())
                    .and(betweenCheckIn(checkIn, checkOut).or(betweenCheckOut(checkIn, checkOut)))
            )
            .fetchFirst();

        return fetchOne != null;
    }

    private BooleanExpression notInCanceled() {
        return reservation.reservationStatus.notIn(ReservationStatus.GUEST_CANCELED, ReservationStatus.GUEST_CANCELED);
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

}
