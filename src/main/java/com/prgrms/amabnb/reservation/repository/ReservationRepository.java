package com.prgrms.amabnb.reservation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prgrms.amabnb.reservation.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {

    @Query("SELECT r FROM Reservation r "
        + "JOIN FETCH r.room ro "
        + "JOIN FETCH ro.host "
        + "WHERE r.id = :reservationId")
    Optional<Reservation> findReservationWithRoomAndHostById(@Param("reservationId") Long reservationId);

    @Query("SELECT r FROM Reservation r "
        + "JOIN FETCH r.room ro "
        + "JOIN FETCH ro.host "
        + "JOIN FETCH r.guest "
        + "WHERE r.id = :reservationId")
    Optional<Reservation> findReservationWithRoomAndHostAndGuestById(@Param("reservationId") Long reservationId);

    @Query("SELECT r FROM Reservation r "
        + "JOIN FETCH r.guest "
        + "WHERE r.id = :reservationId")
    Optional<Reservation> findReservationWithGuestById(@Param("reservationId") Long reservationId);

}
