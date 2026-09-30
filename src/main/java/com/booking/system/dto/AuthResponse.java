package com.booking.system.dto;

public record AuthResponse(String token, String email, String role) {}