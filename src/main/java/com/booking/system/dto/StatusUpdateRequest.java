package com.booking.system.dto;

import com.booking.system.domain.ReservationStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(
    @NotNull(message = "Target processing migration status cannot resolve empty definition block reference")
    ReservationStatus status
) {}