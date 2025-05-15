package com.example.catalog;

import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
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
    void shouldReturnOnlyActiveProducts() {
        String url = "http://localhost:" + port + "/api/products/pageable";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getBody()).contains("Product One");
        assertThat(response.getBody()).contains("Filtered Product");
        assertThat(response.getBody()).doesNotContain("Inactive Product");
    }

    @Test
    void shouldFilterByName() {
        String url = "http://localhost:" + port + "/api/products/pageable?name=filtered";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getBody()).contains("Filtered Product");
        assertThat(response.getBody()).doesNotContain("Product One");
    }

    @Test
    void shouldFilterByMinPrice() {
        String url = "http://localhost:" + port + "/api/products/pageable?minPrice=99.99";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getBody()).contains("Filtered Product");
        assertThat(response.getBody()).doesNotContain("Product One");
    }

    @Test
    void shouldFilterByMinStock() {
        String url = "http://localhost:" + port + "/api/products/pageable?minStock=15";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getBody()).contains("Filtered Product");
        assertThat(response.getBody()).doesNotContain("Product One");
    }
}
