package com.example.catalog.module.order.service;

import com.example.catalog.exception.ForbiddenException;
import com.example.catalog.exception.NotFoundException;
import com.example.catalog.module.auth.model.User;
import com.example.catalog.module.auth.service.UserService;
import com.example.catalog.module.order.dto.response.OrderResponseDTO;
import com.example.catalog.module.order.dto.resquest.OrderItemRequestDTO;
import com.example.catalog.module.order.dto.resquest.OrderRequestDTO;
import com.example.catalog.module.order.mapper.OrderMapper;
import com.example.catalog.module.order.model.Order;
import com.example.catalog.module.order.model.OrderItem;
import com.example.catalog.module.order.repository.OrderRepository;
import com.example.catalog.module.product.model.Product;
import com.example.catalog.module.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {
        User user = userService.getAuthenticatedUser();
        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequestDTO itemDTO : dto.getItems()) {
            Product product = productRepository.findByIdAndActiveTrue(itemDTO.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found or inactive: " + itemDTO.getProductId()));

            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(itemDTO.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();

            items.add(item);
        }

        Order order = Order.builder()
                .user(user)
                .items(new ArrayList<>())
                .build();

        for (OrderItem item : items) {
            item.setOrder(order);
            order.getItems().add(item);
        }

        orderRepository.save(order);

        return orderMapper.toDTO(order);
    }

    public List<OrderResponseDTO> getOrdersForCurrentUser() {
        User user = userService.getAuthenticatedUser();
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream().map(orderMapper::toDTO).toList();
    }

    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        User current = userService.getAuthenticatedUser();
        if (!order.getUser().getId().equals(current.getId())
                && !current.hasRole("ADMIN")
                && !current.hasRole("SUPERVISOR")) {
            throw new ForbiddenException("Access denied");
        }

        return orderMapper.toDTO(order);
    }

    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        User current = userService.getAuthenticatedUser();
        if (!order.getUser().getId().equals(current.getId())
                && !current.hasRole("ADMIN")) {
            throw new ForbiddenException("Access denied");
        }

        order.setCancelled(true);
    }
}