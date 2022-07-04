package com.prgrms.amabnb.reservation.dto.request;

import java.time.LocalDate;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationUpdateRequest {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @NotNull(message = "체크아웃은 비어있을 수 없습니다.")
    @Future(message = "체크아웃은 현재보다 전이거나 현재일 수 없습니다.")
    private LocalDate checkOut;

    @NotNull(message = "총 인원 수는 비어있을 수 없습니다.")
    @Positive(message = "총 인원 수는 양수여야 합니다.")
    private Integer totalGuest;

    @NotNull(message = "추가 가격은 비어있을 수 없습니다.")
    @Positive(message = "추가 가격은 양수여야 합니다.")
    private Integer paymentPrice;

    public ReservationUpdateRequest(LocalDate checkOut, Integer totalGuest, Integer paymentPrice) {
        this.checkOut = checkOut;
        this.totalGuest = totalGuest;
        this.paymentPrice = paymentPrice;
    }

}
