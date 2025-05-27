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
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "Endpoints for managing user's wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserService userService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/{productId}")
    public ResponseEntity<Void> add(@PathVariable UUID productId) {
        User user = userService.getAuthenticatedUser();
        wishlistService.addToWishlist(productId, user);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(@PathVariable UUID productId) {
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

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/contains/{productId}")
    public ResponseEntity<Map<String, Boolean>> contains(@PathVariable UUID productId) {
        User user = userService.getAuthenticatedUser();
        boolean exists = wishlistService.isProductInWishlist(productId, user);
        return ResponseEntity.ok(Map.of("contains", exists));
    }


}