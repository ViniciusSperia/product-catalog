package com.example.catalog.module.wishlist.service;

import com.example.catalog.module.auth.model.User;
import com.example.catalog.module.product.mapper.ProductMapper;
import com.example.catalog.module.product.model.Product;
import com.example.catalog.module.product.repository.ProductRepository;
import com.example.catalog.module.wishlist.dto.response.WishlistItemResponseDTO;
import com.example.catalog.module.wishlist.model.WishlistItem;
import com.example.catalog.module.wishlist.repository.WishlistRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public void addToWishlist(UUID productId, User user) {
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

    @Transactional
    public void removeFromWishlist(UUID productId, User user) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        wishlistRepository.deleteByUserAndProduct(user, product);
    }

    public List<WishlistItem> listWishlist(User user) {
        return wishlistRepository.findByUser(user);
    }

    public List<WishlistItemResponseDTO> getWishlistItems(User user) {
        List<WishlistItem> items = wishlistRepository.findByUser(user);

        return items.stream()
                .map(item -> WishlistItemResponseDTO.builder()
                        .product(productMapper.toResponse(item.getProduct()))
                        .addedAt(item.getAddedAt())
                        .build())
                .toList();
    }

    public boolean isProductInWishlist(UUID productId, User user) {
        return wishlistRepository.findByUserAndProductId(user, productId).isPresent();
    }


}
