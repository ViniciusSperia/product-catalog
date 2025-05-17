package com.example.catalog.modules.auth;

import com.example.catalog.module.auth.dto.request.LoginRequest;
import com.example.catalog.module.auth.dto.request.CreateUserRequest;
import com.example.catalog.module.auth.model.Role;
import com.example.catalog.module.auth.model.User;
import com.example.catalog.module.auth.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private RestTemplate rest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String password = "Admin123!";

    @BeforeEach
    void setUp() {
        // HttpClient sem streaming problemático
        var httpClient = HttpClients.custom()
                .disableContentCompression()
                .build();

        var factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        this.rest = new RestTemplate(factory);

        this.rest.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }
        });

        userRepository.deleteAll();
        var admin = User.builder()
                .name("Admin")
                .email("admin@test.com")
                .password(passwordEncoder.encode(password))
                .role(Role.ADMIN)
                .build();
        userRepository.save(admin);
    }


    @Test
    void shouldLoginWithValidCredentials() {
        var login = new LoginRequest("admin@test.com", password);
        var response = postLogin(login);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("token");
    }

    @Test
    void shouldFailLoginWithInvalidPassword() {
        var login = new LoginRequest("admin@test.com", "WrongPass123");
        var response = postLogin(login);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldCreateUserWhenAuthorized() {
        String token = getTokenFor("admin@test.com", password);

        var request = new CreateUserRequest("Kelly Test", "kelly@test.com", "Strong123!", Role.CUSTOMER);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        var entity = new HttpEntity<>(request, headers);

        var response = rest.exchange(getUrl("/auth/create"), HttpMethod.POST, entity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userRepository.findByEmail("kelly@test.com")).isPresent();
    }

    @Test
    void shouldFailLoginWithNonexistentEmail() {
        var login = new LoginRequest("ghost@test.com", password);
        var response = postLogin(login);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldRejectCreateUserWithoutToken() {
        var request = new CreateUserRequest("Unauthorized", "unauth@test.com", "Strong123!", Role.CUSTOMER);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var entity = new HttpEntity<>(request, headers);

        var response = rest.exchange(getUrl("/auth/create"), HttpMethod.POST, entity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldRejectCreateUserWhenRoleIsForbidden() {
        var vendor = User.builder()
                .name("Vendor")
                .email("vendor@test.com")
                .password(passwordEncoder.encode(password))
                .role(Role.VENDOR)
                .build();
        userRepository.save(vendor);

        String token = getTokenFor("vendor@test.com", password);

        var request = new CreateUserRequest("Blocked", "blocked@test.com", "Strong123!", Role.SUPERVISOR);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        var entity = new HttpEntity<>(request, headers);

        var response = rest.exchange(getUrl("/auth/create"), HttpMethod.POST, entity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldRejectCreateUserWithExistingEmail() {
        String token = getTokenFor("admin@test.com", password);

        var request = new CreateUserRequest("Duplicate", "admin@test.com", "Strong123!", Role.CUSTOMER);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        var entity = new HttpEntity<>(request, headers);

        var response = rest.exchange(getUrl("/auth/create"), HttpMethod.POST, entity, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldRejectLoginWithEmptyEmail() {
        var login = new LoginRequest("", password);
        var response = postLogin(login);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Map> postLogin(LoginRequest login) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var entity = new HttpEntity<>(login, headers);
        return rest.exchange(getUrl("/auth/login"), HttpMethod.POST, entity, Map.class);
    }

    private String getTokenFor(String email, String password) {
        var response = postLogin(new LoginRequest(email, password));
        return (String) response.getBody().get("token");
    }

    private String getUrl(String path) {
        return "http://localhost:" + port + path;
    }
}