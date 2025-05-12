package com.example.catalog;

import com.example.catalog.dto.request.ProductRequest;
import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
}
