package com.example.catalog.modules.product;

import com.example.catalog.module.product.dto.request.ProductRequest;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void clearDatabase() {
        productRepository.deleteAll();
    }

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


    @Test
    void shouldUpdateProductSuccessfully() throws Exception {
        // Create product directly
        Product original = ProductTestFactory.createDefault();
        productRepository.save(original);
        UUID id = original.getId();

        ProductRequest update = new ProductRequest();
        update.setName("Updated Name");
        update.setDescription("Updated Description");
        update.setPrice(new BigDecimal("99.99"));
        update.setStock(50);

        HttpHeaders headers = getAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(update), headers);

        String url = "http://localhost:" + port + "/api/products/" + id;

        ResponseEntity<String> putResponse = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpEntity<Void> getEntity = new HttpEntity<>(getAuthHeaders());
        ResponseEntity<String> getResponse = restTemplate.exchange(
                url,
                HttpMethod.GET,
                getEntity,
                String.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).contains("Updated Name");
        assertThat(getResponse.getBody()).contains("Updated Description");

        Product updated = productRepository.findById(id).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getDescription()).isEqualTo("Updated Description");
        assertThat(updated.getPrice()).isEqualByComparingTo("99.99");
        assertThat(updated.getStock()).isEqualTo(50);
    }
}
