package com.ideasprint.authservice.dto;

public record AuthenticationResponse(
    String token,
    String username,
    String role
) {}
