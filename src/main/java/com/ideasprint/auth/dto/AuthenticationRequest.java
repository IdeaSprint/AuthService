package com.ideasprint.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
    @NotBlank(message = "Username is required")
    String username,

    @NotBlank(message = "Password is required")
    String password
) {}
