package com.booking.system.controller;

import com.booking.system.domain.ReservationStatus;
import com.booking.system.domain.User;
import com.booking.system.dto.ReservationRequest;
import com.booking.system.dto.ReservationResponse;
import com.booking.system.dto.StatusUpdateRequest;
import com.booking.system.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(request, currentUser.getEmail()));
    }

    @GetMapping
    public Page<ReservationResponse> getReservations(
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir,
            @AuthenticationPrincipal User currentUser) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name()) 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        return reservationService.getReservations(status, minPrice, maxPrice, currentUser, pageable);
    }

    @GetMapping("/{id}")
    public ReservationResponse getReservationById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return reservationService.getReservationById(id, currentUser);
    }

    @PutMapping("/{id}/status")
    public ReservationResponse updateReservationStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request,
            @AuthenticationPrincipal User currentUser) {
        return reservationService.updateReservationStatus(id, request.status(), currentUser);
    }
}