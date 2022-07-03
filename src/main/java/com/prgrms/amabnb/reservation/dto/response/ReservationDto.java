package com.prgrms.amabnb.reservation.dto.response;

import java.time.LocalDate;

import com.prgrms.amabnb.reservation.entity.ReservationStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationDto {
    private Long id;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int totalGuest;
    private int totalPrice;
    private ReservationStatus reservationStatus;
    private Long roomId;
    private String roomName;
    private String zipcode;
    private String address;
    private String detailAddress;
    private Long hostId;
    private String name;
    private String email;
}
