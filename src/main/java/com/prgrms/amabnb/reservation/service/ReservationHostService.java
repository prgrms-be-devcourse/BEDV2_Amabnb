package com.prgrms.amabnb.reservation.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.reservation.dto.request.PageReservationRequest;
import com.prgrms.amabnb.reservation.dto.response.ReservationDto;
import com.prgrms.amabnb.reservation.dto.response.ReservationInfoResponse;
import com.prgrms.amabnb.reservation.dto.response.ReservationResponseForHost;
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
        Reservation reservation = findReservationWithRoomAndHostById(reservationId);
        validateHost(host, reservation);
        reservation.changeStatus(ReservationStatus.APPROVED);
        return ReservationInfoResponse.from(reservation);
    }

    @Transactional
    public void cancelByHost(Long userId, Long reservationId) {
        User host = findUserById(userId);
        Reservation reservation = findReservationWithRoomAndHostById(reservationId);
        validateHost(host, reservation);
        reservation.changeStatus(ReservationStatus.HOST_CANCELED);
    }

    public ReservationResponseForHost getReservation(Long userId, Long reservationId) {
        User host = findUserById(userId);
        Reservation reservation = findReservationWithRoomAndHGuestAndGuestById(reservationId);
        validateHost(host, reservation);
        return ReservationResponseForHost.from(reservation);
    }

    public List<ReservationResponseForHost> getReservations(Long userId, PageReservationRequest request) {
        User host = findUserById(userId);
        List<ReservationDto> reservations = reservationRepository.findReservationsByHostAndStatus(
            request.getLastReservationId(),
            request.getPageSize(),
            host,
            request.getStatus()
        );
        return reservations.stream()
            .map(ReservationResponseForHost::from)
            .toList();
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

    private Reservation findReservationWithRoomAndHostById(Long reservationId) {
        return reservationRepository.findReservationWithRoomAndHostById(reservationId)
            .orElseThrow(ReservationNotFoundException::new);
    }

    private Reservation findReservationWithRoomAndHGuestAndGuestById(Long reservationId) {
        return reservationRepository.findReservationWithRoomAndHostAndGuestById(reservationId)
            .orElseThrow(ReservationNotFoundException::new);
    }

}
