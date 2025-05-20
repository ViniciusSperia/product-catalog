package com.example.catalog.module.order.repository;

import com.example.catalog.module.auth.model.User;
import com.example.catalog.module.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}