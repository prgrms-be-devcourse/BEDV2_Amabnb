package com.prgrms.amabnb.reservation.repository;

import com.prgrms.amabnb.reservation.entity.vo.ReservationDate;
import com.prgrms.amabnb.room.entity.Room;

public interface ReservationRepositoryCustom {

    boolean existReservation(Room room, ReservationDate reservationDate);

}
