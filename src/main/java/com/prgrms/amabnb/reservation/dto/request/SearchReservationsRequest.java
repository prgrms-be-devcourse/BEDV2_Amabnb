package com.prgrms.amabnb.reservation.dto.request;

import com.prgrms.amabnb.reservation.entity.ReservationStatus;

import lombok.Getter;

@Getter
public class SearchReservationsRequest {

    private static final int DEFAULT_SIZE = 10;
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 50;

    private int pageSize;
    private ReservationStatus status;
    private Long lastReservationId;

    public SearchReservationsRequest(int pageSize, ReservationStatus status, Long lastReservationId) {
        this.pageSize = checkSize(pageSize);
        this.status = status;
        this.lastReservationId = lastReservationId;
    }

    private int checkSize(int size) {
        if (size > MAX_SIZE || size < MIN_SIZE) {
            return DEFAULT_SIZE;
        }
        return size;
    }

}
