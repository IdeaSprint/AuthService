package com.ideasprint.authservice.dto;

public record UserResponse(
    Long id,
    String username,
    String email,
    String role
) {}
