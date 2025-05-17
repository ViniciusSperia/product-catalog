package com.example.catalog.module.auth.service;

import com.example.catalog.config.security.JwtUtil;
import com.example.catalog.module.auth.dto.request.CreateUserRequest;
import com.example.catalog.module.auth.dto.request.LoginRequest;
import com.example.catalog.module.auth.dto.request.RegisterRequest;
import com.example.catalog.module.auth.dto.response.AuthResponse;
import com.example.catalog.module.auth.model.Role;
import com.example.catalog.module.auth.model.User;
import com.example.catalog.module.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public void register(RegisterRequest request) {
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER) // padrão
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token);
    }

    public void createUser(CreateUserRequest request, Role creatorRole) {
        Role newUserRole = request.getRole();

        boolean permitted =
                switch (creatorRole) {
                    case ADMIN -> true;
                    case SUPERVISOR -> newUserRole == Role.VENDOR || newUserRole == Role.CUSTOMER;
                    case VENDOR -> newUserRole == Role.CUSTOMER;
                    default -> false;
                };

        if (!permitted) {
            throw new AccessDeniedException("You are not allowed to create a user with role: " + newUserRole);
        }

        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(newUserRole)
                .build();

        userRepository.save(user);
    }
}
