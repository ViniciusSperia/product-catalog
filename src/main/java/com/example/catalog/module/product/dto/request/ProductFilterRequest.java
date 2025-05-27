package com.example.catalog.module.product.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterRequest {
    private String name;
    private Double minPrice;
    private Integer minStock;
    private Double maxPrice;
    private UUID categoryId;


    public static ProductFilterRequest of(String name, Double minPrice, Double maxPrice, Integer minStock, UUID categoryId) {
        ProductFilterRequest f = new ProductFilterRequest();
        f.setName((name != null && name.isBlank()) ? null : name);
        f.setMinPrice((minPrice != null && minPrice == 0.0) ? null : minPrice);
        f.setMaxPrice((maxPrice != null && maxPrice == 0.0) ? null : maxPrice);
        f.setMinStock((minStock != null && minStock == 0) ? null : minStock);
        f.setCategoryId(categoryId);
        return f;
    }
}