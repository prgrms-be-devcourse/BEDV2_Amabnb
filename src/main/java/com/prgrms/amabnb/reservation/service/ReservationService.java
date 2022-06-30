package com.prgrms.amabnb.reservation.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.reservation.dto.request.CreateReservationRequest;
import com.prgrms.amabnb.reservation.dto.response.ReservationDateResponse;
import com.prgrms.amabnb.reservation.dto.response.ReservationResponseForGuest;
import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.exception.AlreadyReservationRoomException;
import com.prgrms.amabnb.reservation.exception.AlreadyReservationUserException;
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
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ReservationValidator reservationValidator;

    @Transactional
    public ReservationResponseForGuest createReservation(Long userId, CreateReservationRequest request) {
        Room room = findRoomById(request);
        User guest = findUserById(userId);
        Reservation reservation = request.toEntity(room, guest);
        reservationValidator.validate(reservation);
        isAlreadyReservedRoom(reservation);
        isAlreadyReservedGuest(reservation);
        return ReservationResponseForGuest.from(reservationRepository.save(reservation));
    }

    public List<ReservationDateResponse> getImpossibleReservationDates(
        Long roomId,
        LocalDate startDate,
        LocalDate endDate
    ) {
        return reservationRepository.findImpossibleReservationDate(roomId, startDate, endDate);
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

    private Room findRoomById(CreateReservationRequest request) {
        return roomRepository.findById(request.getRoomId())
            .orElseThrow(RoomNotFoundException::new);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(UserNotFoundException::new);
    }

}
