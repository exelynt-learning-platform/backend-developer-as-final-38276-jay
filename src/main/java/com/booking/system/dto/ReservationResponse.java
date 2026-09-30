package com.booking.system.dto;

import com.booking.system.domain.Reservation;
import com.booking.system.domain.ReservationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationResponse(
    Long id,
    Long userId,
    String userEmail,
    ResourceResponse resource,
    LocalDateTime startTime,
    LocalDateTime endTime,
    BigDecimal totalPrice,
    ReservationStatus status
) {
    public static ReservationResponse fromEntity(Reservation reservation) {
        return new ReservationResponse(
            reservation.getId(),
            reservation.getUser().getId(),
            reservation.getUser().getEmail(),
            ResourceResponse.fromEntity(reservation.getResource()),
            reservation.getStartTime(),
            reservation.getEndTime(),
            reservation.getTotalPrice(),
            reservation.getStatus()
        );
    }
}