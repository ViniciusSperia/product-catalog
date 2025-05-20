package com.example.catalog.module.wishlist.controller;

import com.example.catalog.module.auth.model.User;
import com.example.catalog.module.auth.service.UserService;
import com.example.catalog.module.wishlist.dto.response.WishlistItemResponseDTO;
import com.example.catalog.module.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "Endpoints for managing user's wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserService userService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/{productId}")
    public ResponseEntity<Void> add(@PathVariable Long productId) {
        User user = userService.getAuthenticatedUser();
        wishlistService.addToWishlist(productId, user);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(@PathVariable Long productId) {
        User user = userService.getAuthenticatedUser();
        wishlistService.removeFromWishlist(productId, user);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    public ResponseEntity<List<WishlistItemResponseDTO>> list() {
        User user = userService.getAuthenticatedUser();
        List<WishlistItemResponseDTO> items = wishlistService.getWishlistItems(user);
        return ResponseEntity.ok(items);
    }
}