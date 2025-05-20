package com.example.catalog.modules.product;

import com.example.catalog.module.product.model.Product;
import com.example.catalog.module.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductPaginationIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private HttpHeaders getAuthHeaders() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String json = "{ \"email\": \"admin@test.com\", \"password\": \"admin123\" }";

        HttpEntity<String> request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/auth/login", request, String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String token = objectMapper.readTree(response.getBody()).get("token").asText();

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(token);
        return authHeaders;
    }

    @BeforeEach
    void setup() {
        productRepository.deleteAll();

        Product p1 = ProductTestFactory.create("Product One", new BigDecimal(50), 10);
        Product p2 = ProductTestFactory.create("Filtered Product", new BigDecimal(100), 20);
        Product p3 = ProductTestFactory.create("Inactive Product", new BigDecimal(100), 30);
        p3.setActive(false);

        productRepository.save(p1);
        productRepository.save(p2);
        productRepository.save(p3);
    }

    @Test
    void shouldReturnOnlyActiveProducts() throws Exception {
        String url = "http://localhost:" + port + "/api/products/pageable";
        HttpEntity<Void> entity = new HttpEntity<>(getAuthHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getBody()).contains("Product One");
        assertThat(response.getBody()).contains("Filtered Product");
        assertThat(response.getBody()).doesNotContain("Inactive Product");
    }

    @Test
    void shouldFilterByName() throws Exception {
        String url = "http://localhost:" + port + "/api/products/pageable?name=filtered";
        HttpEntity<Void> entity = new HttpEntity<>(getAuthHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getBody()).contains("Filtered Product");
        assertThat(response.getBody()).doesNotContain("Product One");
    }

    @Test
    void shouldFilterByMinPrice() throws Exception {
        String url = "http://localhost:" + port + "/api/products/pageable?minPrice=99.99";
        HttpEntity<Void> entity = new HttpEntity<>(getAuthHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getBody()).contains("Filtered Product");
        assertThat(response.getBody()).doesNotContain("Product One");
    }

    @Test
    void shouldFilterByMinStock() throws Exception {
        String url = "http://localhost:" + port + "/api/products/pageable?minStock=15";
        HttpEntity<Void> entity = new HttpEntity<>(getAuthHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getBody()).contains("Filtered Product");
        assertThat(response.getBody()).doesNotContain("Product One");
    }
}