package com.example.catalog.module.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) used to receive and validate input data
 * from client requests (e.g., POST or PUT).
 * It does not expose internal entity logic or metadata (e.g., timestamps or database IDs).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating or updating a product")
public class ProductRequest {

    @NotBlank(message = "Name is required")
    @Schema(description = "Product name", example = "Wireless Mouse")
    private String name;

    @Size(max = 255, message = "Description can be at most 255 characters")
    @Schema(description = "Short product description", example = "Ergonomic mouse with USB receiver")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Price must be at least 0.00")
    @Schema(description = "Product price in EUR", example = "29.99")
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    @Schema(description = "Available quantity in stock", example = "150")
    private Integer stock;
}
