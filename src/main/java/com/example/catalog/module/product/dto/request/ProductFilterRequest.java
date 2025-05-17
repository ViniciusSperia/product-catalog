package com.example.catalog.module.product.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterRequest {
    private String name;
    private Double minPrice;
    private Integer minStock;

    public static ProductFilterRequest of(String name, Double minPrice, Integer minStock) {
        ProductFilterRequest f = new ProductFilterRequest();
        f.setName((name != null && name.isBlank()) ? null : name);
        f.setMinPrice((minPrice != null && minPrice == 0.0) ? null : minPrice);
        f.setMinStock((minStock != null && minStock == 0) ? null : minStock);
        return f;
    }
}
