package com.example.catalog;

import com.example.catalog.dto.request.ProductRequest;
import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
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

    @Test
    void fullLifecycleTest_shouldRespectSoftDelete() throws Exception {
        String baseUrl = "http://localhost:" + port + "/api/products";

        // 1. Create Product by POST
        ProductRequest request = new ProductRequest();
        request.setName("IntegrationTest Product");
        request.setPrice(new BigDecimal("49.99"));
        request.setStock(20);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                objectMapper.writeValueAsString(request), headers);

        ResponseEntity<String> postResponse = restTemplate.postForEntity(baseUrl, entity, String.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Long id = objectMapper.readTree(postResponse.getBody()).get("id").asLong();


        // 2. Looking for the product by GET
        ResponseEntity<String> getResponse = restTemplate.getForEntity(baseUrl + "/" + id, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 3. Delete by DELETE
        restTemplate.delete(baseUrl + "/" + id);

        // 4. Try GET again — should now return 404
        ResponseEntity<String> afterDelete = restTemplate.getForEntity(baseUrl + "/" + id, String.class);
        assertThat(afterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);


        // 5. Validate in DB: product is present but inactive
        Product product = productRepository.findById(id).orElseThrow();
        assertThat(product.isActive()).isFalse();
    }

    @BeforeEach
    @Test
    void shouldUpdateProductSuccessfully() throws Exception {
        // Step 1: Save a product directly via repository
        Product original = ProductTestFactory.createDefault();
        productRepository.save(original);

        Long productId = original.getId();

        // Step 2: Create an update request
        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPrice(new BigDecimal("99.99"));
        updateRequest.setStock(50);

        // Step 3: Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                objectMapper.writeValueAsString(updateRequest), headers
        );

        // Step 4: Execute PUT
        String updateUrl = "http://localhost:" + port + "/api/products/" + productId;
        ResponseEntity<String> response = restTemplate.exchange(updateUrl, HttpMethod.PUT, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Step 5: Validate via GET
        ResponseEntity<String> getResponse = restTemplate.getForEntity(updateUrl, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String body = getResponse.getBody();
        assertThat(body).contains("Updated Name");
        assertThat(body).contains("Updated Description");

        // Step 6: Validate via DB
        Product updated = productRepository.findById(productId).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getDescription()).isEqualTo("Updated Description");
        assertThat(updated.getPrice()).isEqualByComparingTo("99.99");
        assertThat(updated.getStock()).isEqualTo(50);
    }
}
