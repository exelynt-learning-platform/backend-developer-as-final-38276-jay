package com.booking.system.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
    int status,
    String message,
    LocalDateTime timestamp,
    Map<String, String> details
) {
    public ErrorResponse(int status, String message) {
        this(status, message, LocalDateTime.now(), null);
    }
    public ErrorResponse(int status, String message, Map<String, String> details) {
        this(status, message, LocalDateTime.now(), details);
    }
}