
package com.example.catalog.module.product.dto.response;

import com.example.catalog.module.category.dto.CategoryDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ProductResponse {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;

    private CategoryDTO category;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}