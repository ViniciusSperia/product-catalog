package com.example.catalog.module.wishlist.controller;

import com.example.catalog.module.auth.model.User;
import com.example.catalog.module.auth.repository.UserRepository;
import com.example.catalog.module.wishlist.dto.response.WishlistResponse;
import com.example.catalog.module.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Wishlist", description = "Endpoints for managing user's wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserRepository userRepository;

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Void> add(@PathVariable Long productId,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        wishlistService.addToWishlist(productId, getCurrentUser(userDetails));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(@PathVariable Long productId,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        wishlistService.removeFromWishlist(productId, getCurrentUser(userDetails));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<WishlistResponse>> list(@AuthenticationPrincipal UserDetails userDetails) {
        List<WishlistResponse> items = wishlistService.getWishlistResponses(getCurrentUser(userDetails));
        return ResponseEntity.ok(items);
    }
}
