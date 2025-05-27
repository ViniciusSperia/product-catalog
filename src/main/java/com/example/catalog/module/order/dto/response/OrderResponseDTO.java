package com.example.catalog.module.order.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private UUID id;
    private UUID userId;
    private LocalDateTime createdAt;
    private boolean cancelled;
    private List<OrderItemResponseDTO> items;
}

