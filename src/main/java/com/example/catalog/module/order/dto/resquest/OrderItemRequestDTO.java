package com.example.catalog.module.order.dto.resquest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) used to receive and validate input data
 * from client requests (e.g., POST or PUT).
 * It does not expose internal entity logic or metadata (e.g., timestamps or database IDs).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequestDTO {
    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
