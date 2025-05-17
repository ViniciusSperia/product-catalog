package com.example.catalog.module.wishlist.service;

import com.example.catalog.module.auth.model.User;
import com.example.catalog.module.product.model.Product;
import com.example.catalog.module.product.repository.ProductRepository;
import com.example.catalog.module.wishlist.dto.response.WishlistResponse;
import com.example.catalog.module.wishlist.model.WishlistItem;
import com.example.catalog.module.wishlist.repository.WishlistRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    public void addToWishlist(Long productId, User user) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        wishlistRepository.findByUserAndProduct(user, product)
                .ifPresent(item -> { throw new IllegalStateException("Product already in wishlist"); });

        WishlistItem item = WishlistItem.builder()
                .user(user)
                .product(product)
                .addedAt(LocalDateTime.now())
                .build();

        wishlistRepository.save(item);
    }

    public void removeFromWishlist(Long productId, User user) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        wishlistRepository.deleteByUserAndProduct(user, product);
    }

    public List<WishlistItem> listWishlist(User user) {
        return wishlistRepository.findByUser(user);
    }


    public List<WishlistResponse> getWishlistResponses(User user) {
        return wishlistRepository.findByUser(user).stream()
                .map(item -> new WishlistResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getDescription(),
                        item.getProduct().getPrice(),
                        item.getAddedAt()
                ))
                .toList();
    }
}
