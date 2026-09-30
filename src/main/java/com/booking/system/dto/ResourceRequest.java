package com.booking.system.dto;

import com.booking.system.domain.ResourceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ResourceRequest(
    @NotBlank(message = "Resource name must not be blank")
    String name,

    @NotNull(message = "Resource type is required")
    ResourceType type,

    @NotNull(message = "Price per hour is required")
    @DecimalMin(value = "0.01", message = "Price per hour must be strictly greater than 0")
    BigDecimal pricePerHour,

    @NotNull(message = "Availability status flag required")
    Boolean available
) {}