
package com.example.catalog.mapper;

import com.example.catalog.dto.request.ProductRequest;
import com.example.catalog.dto.response.ProductResponse;
import com.example.catalog.model.Product;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class ProductMapperTest {

    private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    void shouldMapRequestToEntity() {
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setDescription("A test description");
        request.setPrice(new BigDecimal("29.99"));
        request.setStock(15);

        Product entity = mapper.toEntity(request);

        assertThat(entity.getName()).isEqualTo("Test Product");
        assertThat(entity.getDescription()).isEqualTo("A test description");
        assertThat(entity.getPrice()).isEqualByComparingTo("29.99");
        assertThat(entity.getStock()).isEqualTo(15);
    }

    @Test
    void shouldMapEntityToResponse() {
        Product entity = new Product();
        entity.setId(1L);
        entity.setName("Mapped Product");
        entity.setDescription("Mapped description");
        entity.setPrice(new BigDecimal("49.99"));
        entity.setStock(10);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        ProductResponse response = mapper.toResponse(entity);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Mapped Product");
        assertThat(response.getDescription()).isEqualTo("Mapped description");
        assertThat(response.getPrice()).isEqualByComparingTo("49.99");
        assertThat(response.getStock()).isEqualTo(10);
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();
    }
}
