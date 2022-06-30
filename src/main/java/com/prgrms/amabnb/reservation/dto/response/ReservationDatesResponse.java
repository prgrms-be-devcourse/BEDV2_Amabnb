package com.prgrms.amabnb.reservation.dto.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationDatesResponse {

    private List<ReservationDateResponse> reservationDates;

    public ReservationDatesResponse(List<ReservationDateResponse> reservationDates) {
        this.reservationDates = reservationDates;
    }

}
