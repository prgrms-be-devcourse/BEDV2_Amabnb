package com.prgrms.amabnb.reservation.repository;

import java.time.LocalDate;
import java.util.List;

import com.prgrms.amabnb.reservation.dto.response.ReservationDateResponse;
import com.prgrms.amabnb.reservation.dto.response.ReservationDto;
import com.prgrms.amabnb.reservation.entity.ReservationStatus;
import com.prgrms.amabnb.reservation.entity.vo.ReservationDate;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.user.entity.User;

public interface ReservationRepositoryCustom {

    boolean existReservationByRoom(Room room, Long reservationId, ReservationDate reservationDate);

    List<ReservationDateResponse> findReservationDates(Long roomId, LocalDate startDate, LocalDate endDate);

    List<ReservationDto> findReservationsByGuestAndStatus(
        Long lastReservationId,
        int pageSize,
        User guest,
        ReservationStatus status
    );

    List<ReservationDto> findReservationsByHostAndStatus(
        Long lastReservationId,
        int pageSize,
        User host,
        ReservationStatus status
    );

}
