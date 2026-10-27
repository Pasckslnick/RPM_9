package com.example.userservice.dto;

public record AuthResponse(
        String token,
        String tokenType
) {
}
