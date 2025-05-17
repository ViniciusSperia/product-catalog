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

    @Test
    void shouldCreateAndSoftDeleteProduct() throws Exception {
        String baseUrl = "http://localhost:" + port + "/api/products";

        // Create product
        ProductRequest request = new ProductRequest();
        request.setName("IntegrationTest Product");
        request.setPrice(new BigDecimal("49.99"));
        request.setStock(20);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), headers);

        ResponseEntity<String> postResponse = restTemplate.postForEntity(baseUrl, entity, String.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Long id = objectMapper.readTree(postResponse.getBody()).get("id").asLong();

        // Confirm it's available
        ResponseEntity<String> getResponse = restTemplate.getForEntity(baseUrl + "/" + id, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Delete
        restTemplate.delete(baseUrl + "/" + id);

        // Confirm 404
        ResponseEntity<String> afterDelete = restTemplate.getForEntity(baseUrl + "/" + id, String.class);
        assertThat(afterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        // Confirm still in DB but inactive
        Product product = productRepository.findById(id).orElseThrow();
        assertThat(product.isActive()).isFalse();
    }

    @Test
    void shouldUpdateProductSuccessfully() throws Exception {
        // Create product directly
        Product original = ProductTestFactory.createDefault();
        productRepository.save(original);
        Long id = original.getId();

        ProductRequest update = new ProductRequest();
        update.setName("Updated Name");
        update.setDescription("Updated Description");
        update.setPrice(new BigDecimal("99.99"));
        update.setStock(50);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(update), headers);

        String url = "http://localhost:" + port + "/api/products/" + id;

        ResponseEntity<String> putResponse = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> getResponse = restTemplate.getForEntity(url, String.class);
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
