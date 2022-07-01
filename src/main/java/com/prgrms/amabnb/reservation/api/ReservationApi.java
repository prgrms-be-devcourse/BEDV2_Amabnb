package com.prgrms.amabnb.reservation.api;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.prgrms.amabnb.reservation.dto.request.CreateReservationRequest;
import com.prgrms.amabnb.reservation.dto.request.ReservationDateRequest;
import com.prgrms.amabnb.reservation.dto.response.ReservationDatesResponse;
import com.prgrms.amabnb.reservation.dto.response.ReservationInfoResponse;
import com.prgrms.amabnb.reservation.dto.response.ReservationResponseForGuest;
import com.prgrms.amabnb.reservation.service.ReservationService;
import com.prgrms.amabnb.security.jwt.JwtAuthentication;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReservationApi {

    private final ReservationService reservationService;

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponseForGuest> createReservation(
        @Valid @RequestBody CreateReservationRequest request,
        @AuthenticationPrincipal JwtAuthentication user
    ) {
        ReservationResponseForGuest response = reservationService.createReservation(user.id(), request);
        URI uri = generateUri(response);
        return ResponseEntity
            .created(uri)
            .body(response);
    }

    @GetMapping("/rooms/{roomId}/reservations-date")
    public ResponseEntity<ReservationDatesResponse> getReservationDates(
        @PathVariable Long roomId,
        ReservationDateRequest request
    ) {
        return ResponseEntity.ok(reservationService.getImpossibleReservationDates(roomId, request));
    }

    @PutMapping("hosts/reservations/{reservationId}")
    public ResponseEntity<ReservationInfoResponse> approveReservation(
        @AuthenticationPrincipal JwtAuthentication user,
        @PathVariable Long reservationId
    ) {
        return ResponseEntity.ok(reservationService.approve(user.id(), reservationId));
    }

    @DeleteMapping("hosts/reservations/{reservationId}")
    public ResponseEntity<Void> cancelByHost(
        @AuthenticationPrincipal JwtAuthentication user,
        @PathVariable Long reservationId
    ) {
        reservationService.cancelByHost(user.id(), reservationId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/guests/reservations/{reservationId}")
    public ResponseEntity<Void> cancelByGuest(
        @AuthenticationPrincipal JwtAuthentication user,
        @PathVariable Long reservationId
    ) {
        reservationService.cancelByGuest(user.id(), reservationId);

        return ResponseEntity.noContent().build();
    }

    private URI generateUri(ReservationResponseForGuest response) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{reservationId}")
            .buildAndExpand(response.getReservation().getId())
            .toUri();
    }

}
