package com.example.catalog.controller;

import com.example.catalog.dto.request.ProductFilterRequest;
import com.example.catalog.dto.request.ProductRequest;
import com.example.catalog.dto.response.ProductResponse;
import com.example.catalog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor // Automatically injects final fields via constructor
@Slf4j // Enables logging
@Tag(name = "Products", description = "Endpoints for managing products in the catalog")
public class ProductController {

    private final ProductService productService;

    /**
     * GET /api/products/pageable
     * Returns a paginated list of active products with optional filters.
     * Filters: name (contains), minPrice (>=), minStock (>=)
     * Supports sorting and pagination.
     */
    @GetMapping("/pageable")
    @Operation(summary = "List active products with filters and pagination",
            description = "Returns paginated and filtered list of active products")
    public ResponseEntity<Page<ProductResponse>> getFilteredPaginatedProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        Sort sortObj = Sort.by(Arrays.stream(sort)
                .map(s -> {
                    String[] parts = s.split(",");
                    return new Sort.Order(
                            parts.length > 1 && parts[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                            parts[0]
                    );
                }).toList());

        Pageable pageable = PageRequest.of(page, size, sortObj);
        ProductFilterRequest filters = new ProductFilterRequest();
        filters.setName(name);
        filters.setMinPrice(minPrice);
        filters.setMinStock(minStock);

        log.info("Listing products with filters and pagination");
        return ResponseEntity.ok(productService.filterActiveProducts(filters, pageable));
    }

    /**
     * GET /api/products/{id}
     * Retrieves a single active product by its ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Returns a product by its ID if it's active.")
    public ResponseEntity<ProductResponse> getById(@PathVariable("id") Long id) {
        log.info("Fetching product with ID: {}", id);
        return ResponseEntity.ok(productService.findById(id));
    }

    /**
     * POST /api/products
     * Creates a new product with active = true.
     */
    @PostMapping
    @Operation(summary = "Create a new product", description = "Adds a product to the catalog with active = true.")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        log.info("Creating new product: {}", request.getName());
        return ResponseEntity.ok(productService.save(request));
    }

    /**
     * PUT /api/products/{id}
     * Updates an existing product by ID.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Updates the fields of an existing product by ID.")
    public ResponseEntity<ProductResponse> update(@PathVariable("id") Long id,
                                                  @Valid @RequestBody ProductRequest request) {
        log.info("Updating product with ID: {}", id);
        return ResponseEntity.ok(productService.update(id, request));
    }

    /**
     * DELETE /api/products/{id}
     * Performs a soft delete (sets active = false).
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product (soft delete)", description = "Marks the product as inactive instead of removing it.")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        log.info("Soft-deleting product with ID: {}", id);
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
