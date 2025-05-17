package com.example.catalog.module.wishlist.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WishlistResponse(
        @Schema(example = "12") Long productId,
        @Schema(example = "Wireless Mouse") String name,
        @Schema(example = "Ergonomic Bluetooth Mouse") String description,
        @Schema(example = "49.90") BigDecimal price,
        @Schema(example = "2024-05-18T10:32:15") LocalDateTime addedAt
) {
}
