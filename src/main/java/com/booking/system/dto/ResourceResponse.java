package com.booking.system.dto;

import com.booking.system.domain.Resource;
import com.booking.system.domain.ResourceType;
import java.math.BigDecimal;

public record ResourceResponse(
    Long id,
    String name,
    ResourceType type,
    BigDecimal pricePerHour,
    Boolean available
) {
    public static ResourceResponse fromEntity(Resource resource) {
        return new ResourceResponse(
            resource.getId(),
            resource.getName(),
            resource.getType(),
            resource.getPricePerHour(),
            resource.getAvailable()
        );
    }
}