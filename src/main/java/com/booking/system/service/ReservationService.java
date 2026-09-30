package com.booking.system.service;

import com.booking.system.domain.*;
import com.booking.system.dto.ReservationRequest;
import com.booking.system.dto.ReservationResponse;
import com.booking.system.exception.BadRequestException;
import com.booking.system.exception.ResourceNotFoundException;
import com.booking.system.repository.ReservationRepository;

import com.booking.system.repository.ResourceRepository;
import com.booking.system.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository, ResourceRepository resourceRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Identity tracing mapping references detached token target state mismatch."));

        Resource resource = resourceRepository.findById(request.resourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Target scheduling system unit pointer not found: " + request.resourceId()));

        if (Boolean.FALSE.equals(resource.getAvailable())) {
            throw new BadRequestException("The requested catalog unit item resource configuration is closed to booking reservations.");
        }

        if (request.startTime().isAfter(request.endTime()) || request.startTime().isEqual(request.endTime())) {
            throw new BadRequestException("Chronological time calculation constraint failure: Starting bounds cannot exist past terminal timeline boundary sequences.");
        }

        if (request.startTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Schedule creation operation validation blocked: Starting execution interval coordinates cannot fall behind live context thresholds.");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setResource(resource);
        reservation.setStartTime(request.startTime());
        reservation.setEndTime(request.endTime());
        reservation.setStatus(ReservationStatus.PENDING);

        long hours = (long) Math.ceil(Duration.between(request.startTime(), request.endTime()).toMinutes() / 60.0);
        BigDecimal totalPrice = resource.getPricePerHour().multiply(BigDecimal.valueOf(hours));
        reservation.setTotalPrice(totalPrice);

        return ReservationResponse.fromEntity(reservationRepository.save(reservation));
    }

    public Page<ReservationResponse> getReservations(
            ReservationStatus status, BigDecimal minPrice, BigDecimal maxPrice,
            User currentUser, Pageable pageable) {

        String emailFilter = (currentUser.getRole() == Role.ROLE_USER) ? currentUser.getEmail() : null;
        Page<Reservation> dynamicReservations = reservationRepository.findReservationsWithFilters(
                status,
                emailFilter,
                minPrice,
                maxPrice,
                pageable
        );

        return dynamicReservations.map(ReservationResponse::fromEntity);
    }

    public ReservationResponse getReservationById(Long id, User currentUser) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation ledger data target coordinates not mapped: " + id));

        if (currentUser.getRole() == Role.ROLE_USER && !reservation.getUser().getEmail().equals(currentUser.getEmail())) {
            throw new AccessDeniedException("Data access isolated firewall: Authorization token context domain boundaries mismatched.");
        }

        return ReservationResponse.fromEntity(reservation);
    }

    @Transactional
    public ReservationResponse updateReservationStatus(Long id, ReservationStatus newStatus, User currentUser) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation context targeted for transaction updates not indexed: " + id));

        if (currentUser.getRole() == Role.ROLE_USER) {
            if (!reservation.getUser().getEmail().equals(currentUser.getEmail())) {
                throw new AccessDeniedException("Access boundary enforcement violation.");
            }
            if (newStatus != ReservationStatus.CANCELLED) {
                throw new BadRequestException("User role interaction policies constraint: End users are isolated exclusively to transition states matching CANCELLED.");
            }
        }

        reservation.setStatus(newStatus);
        return ReservationResponse.fromEntity(reservationRepository.save(reservation));
    }
}