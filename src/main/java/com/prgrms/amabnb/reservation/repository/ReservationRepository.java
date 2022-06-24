package com.prgrms.amabnb.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.amabnb.reservation.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
