package com.example.catalog.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) used to receive and validate input data
 * from client requests (e.g., POST or PUT).
 * It does not expose internal entity logic or metadata (e.g., timestamps or database IDs).
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Size(max = 255, message = "Description can be at most 255 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Price must be at least 0.00")
    private BigDecimal price;

    @Min(value = 0, message = "Stock cannot be negative")
    @NotNull(message = "Stock is required")
    private Integer stock;


}
