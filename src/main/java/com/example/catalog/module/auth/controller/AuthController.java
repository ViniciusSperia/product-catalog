package com.example.catalog.module.auth.controller;

import com.example.catalog.module.auth.dto.request.CreateUserRequest;
import com.example.catalog.module.auth.dto.request.LoginRequest;
import com.example.catalog.module.auth.dto.request.RegisterRequest;
import com.example.catalog.module.auth.dto.response.AuthResponse;
import com.example.catalog.module.auth.model.Role;
import com.example.catalog.module.auth.model.User;
import com.example.catalog.module.auth.repository.UserRepository;
import com.example.catalog.module.auth.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user login and registration")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        log.info("Registering new user.");
        authService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        log.info("Login: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDOR')")

    public ResponseEntity<Void> createUser(@RequestBody @Valid CreateUserRequest request,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        Role creatorRole = userRepository.findByEmail(userDetails.getUsername())
                .map(User::getRole)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        authService.createUser(request, creatorRole);
        return ResponseEntity.ok().build();
    }
}
