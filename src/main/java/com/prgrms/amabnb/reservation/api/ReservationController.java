package com.prgrms.amabnb.reservation.api;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.prgrms.amabnb.reservation.dto.request.CreateReservationRequest;
import com.prgrms.amabnb.reservation.dto.response.ReservationResponseForGuest;
import com.prgrms.amabnb.reservation.service.ReservationService;
import com.prgrms.amabnb.security.jwt.JwtAuthentication;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
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

    private URI generateUri(ReservationResponseForGuest response) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{reservationId}")
            .buildAndExpand(response.getId())
            .toUri();
    }

}
