package com.example.catalog.controller;

import com.example.catalog.dto.request.ProductRequest;
import com.example.catalog.dto.response.ProductResponse;
import com.example.catalog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor // Automatically injects final fields via constructor
@Slf4j // Enables logging
@Tag(name = "Products", description = "Endpoints for managing products in the catalog")
public class ProductController {

    private final ProductService service;

    /**
     * GET /api/products
     * Returns a list of all active products.
     */
    @GetMapping
    @Operation(summary = "List all active products", description = "Returns a list of products where active = true.")
    public ResponseEntity<List<ProductResponse>> getAll() {
        log.info("Received request to list all products");
        return ResponseEntity.ok(service.findAll());
    }

    /**
     * GET /api/products/{id}
     * Retrieves a product by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Returns a product by its ID if it's active.")
    public ResponseEntity<ProductResponse> getById(@PathVariable("id") Long id) {
        log.info("Fetching product with ID: {}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * POST /api/products
     * Creates a new product.
     */
    @PostMapping
    @Operation(summary = "Create a new product", description = "Adds a product to the catalog with active = true.")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        log.info("Creating new product: {}", request.getName());
        return ResponseEntity.ok(service.save(request));
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
        return ResponseEntity.ok(service.update(id, request));
    }

    /**
     * DELETE /api/products/{id}
     * Performs a soft delete (marks as inactive).
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product (soft delete)", description = "Marks the product as inactive instead of removing it.")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        log.info("Soft-deleting product with ID: {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
