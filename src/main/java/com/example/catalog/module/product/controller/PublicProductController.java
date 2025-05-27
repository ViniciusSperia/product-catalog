package com.example.catalog.module.product.controller;

import com.example.catalog.module.product.dto.request.ProductFilterRequest;
import com.example.catalog.module.product.dto.response.ProductResponse;
import com.example.catalog.module.product.dto.response.ProductSummaryDto;
import com.example.catalog.module.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/public/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public Products", description = "Public endpoints to explore catalog products")
public class PublicProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Public product search", description = "Returns a lightweight list of products with filters and pagination.")
    public ResponseEntity<Page<ProductSummaryDto>> searchPublicProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("Public search for products with filters");

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(new Sort.Order(sortDirection, sortField)));

        ProductFilterRequest filters = ProductFilterRequest.of(name, minPrice, maxPrice, minStock, categoryId);
        return ResponseEntity.ok(productService.findPublicProducts(filters, pageable));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get public product by slug", description = "Returns full product data using the product slug")
    public ResponseEntity<ProductResponse> getPublicProductBySlug(@PathVariable String slug) {
        log.info("Fetching public product with slug: {}", slug);
        return ResponseEntity.ok(productService.findPublicBySlug(slug));
    }

}
