package com.prgrms.amabnb.reservation.dto.request;

import static com.prgrms.amabnb.reservation.entity.ReservationStatus.*;

import java.time.LocalDate;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.prgrms.amabnb.common.vo.Money;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @NotNull(message = "체크인은 비어있을 수 없습니다.")
    @FutureOrPresent(message = "체크인은 현재보다 전일 수 없습니다.")
    private LocalDate checkIn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @NotNull(message = "체크아웃은 비어있을 수 없습니다.")
    @Future(message = "체크아웃은 현재보다 전이거나 현재일 수 없습니다.")
    private LocalDate checkOut;

    @NotNull(message = "총 인원 수는 비어있을 수 없습니다.")
    @Positive(message = "총 인원 수는 양수여야 합니다.")
    private Integer totalGuest;

    @NotNull(message = "총 가격은 비어있을 수 없습니다.")
    @Positive(message = "총 가격은 양수여야 합니다.")
    private Integer totalPrice;

    @NotNull(message = "숙소 아이디는 비어있을 수 없습니다.")
    @Positive(message = "숙소 아이디는 양수여야 합니다.")
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
            .reservationStatus(PENDING)
            .room(room)
            .guest(guest)
            .build();
    }
}
