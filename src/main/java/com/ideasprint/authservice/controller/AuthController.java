package com.ideasprint.authservice.controller;

import com.ideasprint.authservice.dto.AuthenticationRequest;
import com.ideasprint.authservice.dto.AuthenticationResponse;
import com.ideasprint.authservice.dto.RegisterRequest;
import com.ideasprint.authservice.dto.UserResponse;
import com.ideasprint.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get token")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/users/{username}")
    @Operation(summary = "Get user details by username")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(authService.getUserByUsername(username));
    }
}

