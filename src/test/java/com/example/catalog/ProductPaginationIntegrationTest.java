package com.example.catalog;

import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductPaginationIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setup() {
        productRepository.deleteAll();

        Product p1 = new Product("Product One", new BigDecimal("50.00"), 10);
        Product p2 = new Product("Filtered Product", new BigDecimal("100.00"), 20);
        Product p3 = new Product("Inactive Product", new BigDecimal("100.00"), 30);
        p3.setActive(false);

        productRepository.save(p1);
        productRepository.save(p2);
        productRepository.save(p3);
    }

    @Test
    void shouldReturnFilteredPaginatedProducts() {
        String baseUrl = "http://localhost:" + port + "/api/products/pageable";
        String urlWithParams = baseUrl + "?name=Filtered&minPrice=50&minStock=10&page=0&size=10&sortField=name&direction=asc";

        ResponseEntity<Map> response = restTemplate.getForEntity(urlWithParams, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("content")).isInstanceOf(java.util.List.class);

        var products = (java.util.List<?>) body.get("content");

        // Only "Filtered Product" should match
        assertThat(products).hasSize(1);
        Map<String, Object> product = (Map<String, Object>) products.get(0);
        assertThat(product.get("name")).isEqualTo("Filtered Product");
        assertThat(product.get("price")).isEqualTo(100.00);
    }
}
