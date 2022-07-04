package com.prgrms.amabnb.reservation.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.amabnb.common.model.ApiResponse;
import com.prgrms.amabnb.reservation.dto.request.SearchReservationsRequest;
import com.prgrms.amabnb.reservation.dto.response.ReservationInfoResponse;
import com.prgrms.amabnb.reservation.dto.response.ReservationResponseForHost;
import com.prgrms.amabnb.reservation.service.ReservationHostService;
import com.prgrms.amabnb.security.jwt.JwtAuthentication;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReservationHostApi {

    private final ReservationHostService reservationHostService;

    @GetMapping("/host/reservations/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationResponseForHost>> getReservation(
        @AuthenticationPrincipal JwtAuthentication user,
        @PathVariable Long reservationId
    ) {
        return ResponseEntity.ok(new ApiResponse<>(reservationHostService.getReservation(user.id(), reservationId)));
    }

    @GetMapping("/host/reservations")
    public ResponseEntity<ApiResponse<List<ReservationResponseForHost>>> getReservations(
        @AuthenticationPrincipal JwtAuthentication user,
        SearchReservationsRequest request
    ) {
        return ResponseEntity.ok(new ApiResponse<>(reservationHostService.getReservations(user.id(), request)));
    }

    @PutMapping("/host/reservations/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationInfoResponse>> approveReservation(
        @AuthenticationPrincipal JwtAuthentication user,
        @PathVariable Long reservationId
    ) {
        return ResponseEntity.ok(new ApiResponse<>(reservationHostService.approve(user.id(), reservationId)));
    }

    @DeleteMapping("/host/reservations/{reservationId}")
    public ResponseEntity<Void> cancelByHost(
        @AuthenticationPrincipal JwtAuthentication user,
        @PathVariable Long reservationId
    ) {
        reservationHostService.cancelByHost(user.id(), reservationId);

        return ResponseEntity.noContent().build();
    }

}
