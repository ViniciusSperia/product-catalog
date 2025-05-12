package com.example.catalog.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductFilterRequest {
    private String name;
    private Double minPrice;
    private Integer minStock;

}
