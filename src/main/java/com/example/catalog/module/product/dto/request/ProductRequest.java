package com.example.catalog.module.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

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

    @NotBlank(message = "Image URL is required")
    @Schema(description = "URL for the product image", example = "https://example.com/images/product.jpg")
    private String imageUrl;

    @NotNull(message = "Category ID is required")
    @Schema(description = "UUID of the product category", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID categoryId;
}
