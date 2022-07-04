package com.prgrms.amabnb.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.reservation.dto.response.ReservationInfoResponse;
import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.ReservationStatus;
import com.prgrms.amabnb.reservation.exception.ReservationNotFoundException;
import com.prgrms.amabnb.reservation.exception.ReservationNotHavePermissionException;
import com.prgrms.amabnb.reservation.repository.ReservationRepository;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.exception.UserNotFoundException;
import com.prgrms.amabnb.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationHostService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReservationInfoResponse approve(Long userId, Long reservationId) {
        User host = findUserById(userId);
        Reservation reservation = findReservationWithRoom(reservationId);
        validateHost(host, reservation);
        reservation.changeStatus(ReservationStatus.APPROVED);
        return ReservationInfoResponse.from(reservation);
    }

    @Transactional
    public void cancelByHost(Long userId, Long reservationId) {
        User host = findUserById(userId);
        Reservation reservation = findReservationWithRoom(reservationId);
        validateHost(host, reservation);
        reservation.changeStatus(ReservationStatus.HOST_CANCELED);
    }

    private void validateHost(User host, Reservation reservation) {
        if (reservation.isNotHost(host)) {
            throw new ReservationNotHavePermissionException("해당 예약의 호스트가 아닙니다.");
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(UserNotFoundException::new);
    }

    private Reservation findReservationWithRoom(Long reservationId) {
        return reservationRepository.findReservationWithRoomById(reservationId)
            .orElseThrow(ReservationNotFoundException::new);
    }

}
