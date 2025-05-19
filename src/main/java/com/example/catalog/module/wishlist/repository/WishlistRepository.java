package com.example.catalog.module.wishlist.repository;

import com.example.catalog.module.auth.model.User;
import com.example.catalog.module.product.model.Product;
import com.example.catalog.module.wishlist.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {

    List<WishlistItem> findByUser(User user);

    List<WishlistItem> findAllByUserId(Long userId);

    Optional<WishlistItem> findByUserAndProduct(User user, Product product);

    void deleteByUserAndProduct(User user, Product product);
}
