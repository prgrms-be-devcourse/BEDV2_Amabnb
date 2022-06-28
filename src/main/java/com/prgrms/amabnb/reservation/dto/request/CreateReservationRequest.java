package com.prgrms.amabnb.reservation.dto.request;

import java.time.LocalDate;

import com.prgrms.amabnb.common.model.Money;
import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.vo.ReservationDate;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.user.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateReservationRequest {

    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer totalGuest;
    private Integer totalPrice;
    private Long roomId;

    @Builder
    public CreateReservationRequest(LocalDate checkIn, LocalDate checkOut, Integer totalGuest, Integer totalPrice,
        Long roomId) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalGuest = totalGuest;
        this.totalPrice = totalPrice;
        this.roomId = roomId;
    }

    public Reservation toEntity(Room room, User guest) {
        return Reservation.builder()
            .reservationDate(new ReservationDate(checkIn, checkOut))
            .totalGuest(totalGuest)
            .totalPrice(new Money(totalPrice))
            .room(room)
            .guest(guest)
            .build();
    }
}
