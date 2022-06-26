package com.prgrms.amabnb.reservation.entity.vo;

import java.time.LocalDate;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;

import com.prgrms.amabnb.reservation.exception.ReservationInvalidValueException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Access(AccessType.FIELD)
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationDate {

    private LocalDate checkIn;
    private LocalDate checkOut;

    public ReservationDate(LocalDate checkIn, LocalDate checkOut) {
        validateNull(checkIn, checkOut);
        validateBeforeNowCheckIn(checkIn);
        validateBeforeOrSameNowCheckOut(checkOut);
        validateDate(checkIn, checkOut);
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public int getPeriod() {
        return checkIn.until(checkOut).getDays();
    }

    private void validateNull(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new ReservationInvalidValueException("예약 날짜는 비어있을 수 없습니다.");
        }
    }

    private void validateBeforeNowCheckIn(LocalDate checkIn) {
        if (checkIn.isBefore(LocalDate.now())) {
            throw new ReservationInvalidValueException("체크인은 현재보다 전일 수 없습니다.");
        }
    }

    private void validateBeforeOrSameNowCheckOut(LocalDate checkOut) {
        if (checkOut.isBefore(LocalDate.now()) || checkOut.isEqual(LocalDate.now())) {
            throw new ReservationInvalidValueException("체크아웃은 현재보다 전이거나 현재일 수 없습니다.");
        }
    }

    private void validateDate(LocalDate checkIn, LocalDate checkOut) {
        if (checkOut.isBefore(checkIn) || checkIn.isEqual(checkOut)) {
            throw new ReservationInvalidValueException("체크 아웃 날짜가 체크인 날짜 전이거나 같을 수 없습니다.");
        }
    }

}
