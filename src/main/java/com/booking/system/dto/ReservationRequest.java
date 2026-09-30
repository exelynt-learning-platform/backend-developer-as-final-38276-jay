package com.booking.system.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ReservationRequest(
    @NotNull(message = "Resource item context ID constraint violation missing reference marker")
    Long resourceId,

    @NotNull(message = "Target allocation processing starting block cannot map empty space context")
    LocalDateTime startTime,

    @NotNull(message = "Terminal boundary schedule map timeline sequence element missing terminal element")
    LocalDateTime endTime
) {}