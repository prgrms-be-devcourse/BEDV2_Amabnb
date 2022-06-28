package com.prgrms.amabnb.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.reservation.dto.request.CreateReservationRequest;
import com.prgrms.amabnb.reservation.dto.response.ReservationResponseForGuest;
import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.exception.AlreadyReservationRoomException;
import com.prgrms.amabnb.reservation.exception.AlreadyReservationUserException;
import com.prgrms.amabnb.reservation.exception.ReservationInvalidValueException;
import com.prgrms.amabnb.reservation.repository.ReservationRepository;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.exception.RoomNotFoundException;
import com.prgrms.amabnb.room.repository.CreateRoomRepository;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.exception.UserNotFoundException;
import com.prgrms.amabnb.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final CreateRoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReservationResponseForGuest createReservation(Long userId, CreateReservationRequest request) {
        Room room = findRoomById(request);
        User guest = findUserById(userId);
        Reservation reservation = request.toEntity(room, guest);
        validateReservation(reservation);
        return ReservationResponseForGuest.from(reservationRepository.save(reservation));
    }

    private void validateReservation(Reservation reservation) {
        validateRoomPrice(reservation);
        validateMaxGuest(reservation);
        validateAlreadyReservationRoom(reservation);
        validateAlreadyReservationGuest(reservation);
    }

    private void validateRoomPrice(Reservation reservation) {
        // TODO : room을 밖으로 빼는 것이 좋을 지?
        // TODO : 예외 클래스를 하나 더 만들어줘야할지?
        Room room = reservation.getRoom();
        int period = reservation.getReservationDate().getPeriod();
        if (!room.isValidatePrice(reservation.getTotalPrice(), period)) {
            throw new ReservationInvalidValueException("숙소 가격이 일치하지 않습니다.");
        }
    }

    private void validateMaxGuest(Reservation reservation) {
        Room room = reservation.getRoom();
        if (room.isOverMaxGuestNum(reservation.getTotalGuest())) {
            throw new ReservationInvalidValueException("숙소의 최대 인원을 넘을 수 없습니다.");
        }
    }

    private void validateAlreadyReservationRoom(Reservation reservation) {
        if (reservationRepository.existReservation(reservation.getRoom(), reservation.getReservationDate())) {
            throw new AlreadyReservationRoomException();
        }
    }

    private void validateAlreadyReservationGuest(Reservation reservation) {
        if (reservationRepository.existReservationByGuest(reservation.getGuest(), reservation.getReservationDate())) {
            throw new AlreadyReservationUserException();
        }
    }

    private Room findRoomById(CreateReservationRequest request) {
        return roomRepository.findById(request.getRoomId())
            .orElseThrow(RoomNotFoundException::new);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(UserNotFoundException::new);
    }

}
