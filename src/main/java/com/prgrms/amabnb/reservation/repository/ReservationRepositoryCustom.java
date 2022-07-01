package com.prgrms.amabnb.reservation.repository;

import java.time.LocalDate;
import java.util.List;

import com.prgrms.amabnb.reservation.dto.response.ReservationDateResponse;
import com.prgrms.amabnb.reservation.entity.vo.ReservationDate;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.user.entity.User;

public interface ReservationRepositoryCustom {

    boolean existReservation(Room room, ReservationDate reservationDate);

    boolean existReservationByGuest(User guest, ReservationDate reservationDate);

    List<ReservationDateResponse> findReservationDates(Long roomId, LocalDate startDate, LocalDate endDate);
}
