package com.example.catalog.module.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ProductSummaryDto {
    private UUID id;
    private String name;
    private BigDecimal price;
    private String imageUrl;
    private String categoryName;
    private String slug;
}
