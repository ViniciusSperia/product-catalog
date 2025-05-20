package com.example.catalog.module.order.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private Long id;
    private Long userId;
    private LocalDateTime createdAt;
    private boolean cancelled;
    private List<OrderItemResponseDTO> items;
}

