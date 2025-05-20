package com.example.catalog.module.order.mapper;

import com.example.catalog.module.order.dto.response.OrderItemResponseDTO;
import com.example.catalog.module.order.dto.response.OrderResponseDTO;
import com.example.catalog.module.order.model.Order;
import com.example.catalog.module.order.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    public OrderResponseDTO toDTO(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .createdAt(order.getCreatedAt())
                .cancelled(order.isCancelled())
                .items(order.getItems().stream().map(this::toItemDTO).toList())
                .build();
    }

    private OrderItemResponseDTO toItemDTO(OrderItem item) {
        BigDecimal total = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return OrderItemResponseDTO.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .total(total)
                .build();
    }
}
