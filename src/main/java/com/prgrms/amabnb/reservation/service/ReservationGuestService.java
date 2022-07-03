package com.prgrms.amabnb.reservation.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.reservation.dto.request.CreateReservationRequest;
import com.prgrms.amabnb.reservation.dto.request.PageReservationRequest;
import com.prgrms.amabnb.reservation.dto.request.ReservationDateRequest;
import com.prgrms.amabnb.reservation.dto.response.ReservationDateResponse;
import com.prgrms.amabnb.reservation.dto.response.ReservationDto;
import com.prgrms.amabnb.reservation.dto.response.ReservationResponseForGuest;
import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.ReservationStatus;
import com.prgrms.amabnb.reservation.exception.AlreadyReservationRoomException;
import com.prgrms.amabnb.reservation.exception.AlreadyReservationUserException;
import com.prgrms.amabnb.reservation.exception.ReservationInvalidValueException;
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

    @Transactional
    public ReservationResponseForGuest createReservation(Long userId, CreateReservationRequest request) {
        Room room = findRoomWithHostById(request.getRoomId());
        User guest = findUserById(userId);
        Reservation reservation = request.toEntity(room, guest);
        validateReservation(reservation);
        return ReservationResponseForGuest.from(reservationRepository.save(reservation));
    }

    @Transactional
    public void cancel(Long userId, Long reservationId) {
        User guest = findUserById(userId);
        Reservation reservation = findReservationWithGuestById(reservationId);
        validateGuest(guest, reservation);
        reservation.changeStatus(ReservationStatus.GUEST_CANCELED);
    }

    public List<ReservationDateResponse> getReservationDates(Long roomId, ReservationDateRequest request) {
        return reservationRepository.findReservationDates(roomId, request.getStartDate(), request.getEndDate());
    }

    public ReservationResponseForGuest getReservation(Long userId, Long reservationId) {
        User guest = findUserById(userId);
        Reservation reservation = findReservationWithRoomAndUsersById(reservationId);
        validateGuest(guest, reservation);
        return ReservationResponseForGuest.from(reservation);
    }

    public List<ReservationResponseForGuest> getReservations(Long userId, PageReservationRequest request) {
        User guest = findUserById(userId);
        List<ReservationDto> reservations = reservationRepository.findReservationsByGuestAndStatus(
            request.getLastReservationId(),
            request.getPageSize(),
            guest,
            request.getStatus()
        );
        return reservations.stream()
            .map(ReservationResponseForGuest::from)
            .toList();
    }

    private void validateReservation(Reservation reservation) {
        validateRoomPrice(reservation);
        validateMaxGuest(reservation);
        validateAlreadyReservedRoom(reservation);
        validateAlreadyReservedGuest(reservation);
    }

    private void validateRoomPrice(Reservation reservation) {
        if (reservation.isNotValidatePrice()) {
            throw new ReservationInvalidValueException("숙소 가격이 일치하지 않습니다.");
        }
    }

    private void validateMaxGuest(Reservation reservation) {
        if (reservation.isOverMaxGuest()) {
            throw new ReservationInvalidValueException("숙소의 최대 인원을 넘을 수 없습니다.");
        }
    }

    private void validateAlreadyReservedRoom(Reservation reservation) {
        if (reservationRepository.existReservationByRoom(reservation.getRoom(), reservation.getReservationDate())) {
            throw new AlreadyReservationRoomException();
        }
    }

    private void validateAlreadyReservedGuest(Reservation reservation) {
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

    private Reservation findReservationWithGuestById(Long reservationId) {
        return reservationRepository.findReservationWithGuestById(reservationId)
            .orElseThrow(ReservationNotFoundException::new);
    }

    private Reservation findReservationWithRoomAndUsersById(Long reservationId) {
        return reservationRepository.findReservationWithRoomAndHostAndGuestById(reservationId)
            .orElseThrow(ReservationNotFoundException::new);
    }

}
