package com.prgrms.amabnb.reservation.api;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.prgrms.amabnb.common.model.ApiResponse;
import com.prgrms.amabnb.reservation.dto.request.CreateReservationRequest;
import com.prgrms.amabnb.reservation.dto.request.ReservationDateRequest;
import com.prgrms.amabnb.reservation.dto.response.ReservationDateResponse;
import com.prgrms.amabnb.reservation.dto.response.ReservationResponseForGuest;
import com.prgrms.amabnb.reservation.service.ReservationGuestService;
import com.prgrms.amabnb.security.jwt.JwtAuthentication;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReservationGuestApi {
    private final ReservationGuestService reservationService;

    @PostMapping("/reservations")
    public ResponseEntity<ApiResponse<ReservationResponseForGuest>> createReservation(
        @Valid @RequestBody CreateReservationRequest request,
        @AuthenticationPrincipal JwtAuthentication user
    ) {
        ReservationResponseForGuest response = reservationService.createReservation(user.id(), request);
        URI uri = generateUri(response);
        return ResponseEntity
            .created(uri)
            .body(new ApiResponse<>(response));
    }

    @GetMapping("/rooms/{roomId}/reservations-date")
    public ResponseEntity<ApiResponse<List<ReservationDateResponse>>> getReservationDates(
        @PathVariable Long roomId,
        ReservationDateRequest request
    ) {
        return ResponseEntity.ok(new ApiResponse<>(reservationService.getReservationDates(roomId, request)));
    }

    @DeleteMapping("/guest/reservations/{reservationId}")
    public ResponseEntity<Void> cancel(
        @AuthenticationPrincipal JwtAuthentication user,
        @PathVariable Long reservationId
    ) {
        reservationService.cancel(user.id(), reservationId);

        return ResponseEntity.noContent().build();
    }

    private URI generateUri(ReservationResponseForGuest response) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{reservationId}")
            .buildAndExpand(response.getReservation().getId())
            .toUri();
    }

}
