package com.prgrms.amabnb.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.reservation.dto.request.CreateReservationRequest;
import com.prgrms.amabnb.reservation.dto.request.ReservationDateRequest;
import com.prgrms.amabnb.reservation.dto.response.ReservationDatesResponse;
import com.prgrms.amabnb.reservation.dto.response.ReservationResponseForGuest;
import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.ReservationStatus;
import com.prgrms.amabnb.reservation.exception.AlreadyReservationRoomException;
import com.prgrms.amabnb.reservation.exception.AlreadyReservationUserException;
import com.prgrms.amabnb.reservation.exception.ReservationNotFoundException;
import com.prgrms.amabnb.reservation.exception.ReservationNotHavePermissionException;
import com.prgrms.amabnb.reservation.repository.ReservationRepository;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.exception.RoomNotFoundException;
import com.prgrms.amabnb.room.repository.RoomRepository;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.exception.UserNotFoundException;
import com.prgrms.amabnb.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationGuestService {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ReservationValidator reservationValidator;

    @Transactional
    public ReservationResponseForGuest createReservation(Long userId, CreateReservationRequest request) {
        Room room = findRoomWithHostById(request.getRoomId());
        User guest = findUserById(userId);
        Reservation reservation = request.toEntity(room, guest);
        reservationValidator.validate(reservation);
        isAlreadyReservedRoom(reservation);
        isAlreadyReservedGuest(reservation);
        return ReservationResponseForGuest.from(reservationRepository.save(reservation));
    }

    public ReservationDatesResponse getReservationDates(Long roomId, ReservationDateRequest request) {
        return new ReservationDatesResponse(
            reservationRepository.findReservationDates(roomId, request.getStartDate(), request.getEndDate())
        );
    }

    @Transactional
    public void cancel(Long userId, Long reservationId) {
        User guest = findUserById(userId);
        Reservation reservation = findReservationWithGuest(reservationId);
        validateGuest(guest, reservation);
        reservation.changeStatus(ReservationStatus.GUEST_CANCELED);
    }

    private void isAlreadyReservedRoom(Reservation reservation) {
        if (reservationRepository.existReservation(reservation.getRoom(), reservation.getReservationDate())) {
            throw new AlreadyReservationRoomException();
        }
    }

    private void isAlreadyReservedGuest(Reservation reservation) {
        if (reservationRepository.existReservationByGuest(reservation.getGuest(), reservation.getReservationDate())) {
            throw new AlreadyReservationUserException();
        }
    }

    private void validateGuest(User host, Reservation reservation) {
        if (reservation.isNotGuest(host)) {
            throw new ReservationNotHavePermissionException("해당 예약의 게스트가 아닙니다.");
        }
    }

    private Room findRoomWithHostById(Long roomId) {
        return roomRepository.findRoomWithHostById(roomId)
            .orElseThrow(RoomNotFoundException::new);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(UserNotFoundException::new);
    }

    private Reservation findReservationWithGuest(Long reservationId) {
        return reservationRepository.findReservationWithGuestById(reservationId)
            .orElseThrow(ReservationNotFoundException::new);
    }

}
