package com.example.catalog.module.wishlist.dto.response;

import com.example.catalog.module.product.dto.response.ProductResponse;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistItemResponseDTO {
    private ProductResponse product;
    private LocalDateTime addedAt;
}
