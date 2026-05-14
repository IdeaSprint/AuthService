package com.ideasprint.authservice.service;

import com.ideasprint.authservice.dto.AuthenticationRequest;
import com.ideasprint.authservice.dto.AuthenticationResponse;
import com.ideasprint.authservice.dto.RegisterRequest;
import com.ideasprint.authservice.dto.UserResponse;
import com.ideasprint.authservice.model.User;
import com.ideasprint.authservice.repository.UserRepository;
import com.ideasprint.authservice.security.JwtUtil;
import com.ideasprint.exception.BusinessException;
import com.ideasprint.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    private Environment env;

    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(env.getProperty("error.username.exists"));
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(env.getProperty("error.email.exists"));
        }

        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(User.Role.USER)
                .build();

        userRepository.save(user);
        var jwtToken = jwtUtil.generateToken(user);
        return new AuthenticationResponse(jwtToken, user.getUsername(), user.getRole().name());
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        var user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(env.getProperty("error.user.not.found")));
        var jwtToken = jwtUtil.generateToken(user);
        return new AuthenticationResponse(jwtToken, user.getUsername(), user.getRole().name());
    }

    public UserResponse getUserByUsername(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(env.getProperty("error.user.not.found.username"), username)));
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }
}
