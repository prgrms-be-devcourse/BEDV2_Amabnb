package com.prgrms.amabnb.reservation.dto.response;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationDateResponse {

    private LocalDate checkIn;
    private LocalDate checkOut;

    public ReservationDateResponse(LocalDate checkIn, LocalDate checkOut) {
        this.checkIn = checkIn;
        this.checkOut = checkOut.minusDays(1);
    }

}
