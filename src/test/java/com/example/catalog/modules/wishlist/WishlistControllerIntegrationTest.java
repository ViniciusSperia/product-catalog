package com.example.catalog.modules.wishlist;

import com.example.catalog.module.auth.model.Role;
import com.example.catalog.module.auth.model.User;
import com.example.catalog.module.auth.repository.UserRepository;
import com.example.catalog.module.product.model.Product;
import com.example.catalog.module.product.repository.ProductRepository;
import com.example.catalog.module.wishlist.model.WishlistItem;
import com.example.catalog.module.wishlist.repository.WishlistRepository;
import com.example.catalog.module.auth.dto.request.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WishlistControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RestTemplate rest;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String password = "Test123!";

    @BeforeEach
    void setUp() {
        SimpleClientHttpRequestFactory simpleFactory = new SimpleClientHttpRequestFactory();
        simpleFactory.setOutputStreaming(false);
        var factory = new BufferingClientHttpRequestFactory(simpleFactory);
        this.rest = new RestTemplate(factory);
        this.rest.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }
        });

        wishlistRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        var user = User.builder()
                .name("User")
                .email("user@test.com")
                .password(passwordEncoder.encode(password))
                .role(Role.CUSTOMER)
                .build();
        userRepository.save(user);

        Product product = Product.builder()
                .name("Headphones")
                .description("Bluetooth headphones")
                .price(BigDecimal.valueOf(150.0))
                .stock(10)
                .active(true)
                .build();
        productRepository.save(product);
    }

    @Test
    void shouldAddToWishlist() {
        String token = getTokenFor("user@test.com", password);
        Long productId = productRepository.findAll().get(0).getId();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        var response = rest.exchange(getUrl("/wishlist/" + productId), HttpMethod.POST, entity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        User user = userRepository.findByEmail("user@test.com").orElseThrow();
        assertThat(wishlistRepository.findAllByUserId(user.getId())).hasSize(1);
    }

    @Test
    void shouldListWishlist() {
        User user = userRepository.findByEmail("user@test.com").orElseThrow();
        Product product = productRepository.findAll().get(0);

        wishlistRepository.save(WishlistItem.builder().user(user).product(product).build());

        String token = getTokenFor("user@test.com", password);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List> response = rest.exchange(
                getUrl("/wishlist"),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void shouldRemoveFromWishlist() {
        User user = userRepository.findByEmail("user@test.com").orElseThrow();
        Product product = productRepository.findAll().get(0);
        WishlistItem wishlist = wishlistRepository.save(WishlistItem.builder().user(user).product(product).build());

        String token = getTokenFor("user@test.com", password);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        var response = rest.exchange(getUrl("/wishlist/" + product.getId()), HttpMethod.DELETE, entity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(wishlistRepository.findAllByUserId(user.getId())).isEmpty();
    }

    private String getUrl(String path) {
        return "http://localhost:" + port + path;
    }

    private String getTokenFor(String email, String password) {
        LoginRequest login = new LoginRequest(email, password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var entity = new HttpEntity<>(login, headers);
        var response = rest.exchange(getUrl("/auth/login"), HttpMethod.POST, entity, Map.class);
        return (String) response.getBody().get("token");
    }
}