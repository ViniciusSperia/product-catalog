package com.example.catalog.controller;

import com.example.catalog.model.dto.ProductDto;
import com.example.catalog.model.Product;
import com.example.catalog.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller that exposes endpoints for managing products.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    @Autowired // Dependency injection via constructor
    public ProductController(ProductService service) {
        this.service = service;
    }

    /**
     * GET /api/products
     * Returns a list of all products.
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    /**
     * GET /api/products/{id}
     * Returns a single product by ID, or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/products
     * Creates a new product from a ProductDto.
     */
    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody ProductDto dto) {
        Product saved = service.save(dto);
        return ResponseEntity.ok(saved);
    }

    /**
     * PUT /api/products/{id}
     * Updates a product by ID. Returns 404 if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody ProductDto dto) {
        Product updated = service.update(id, dto);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/products/{id}
     * Deletes a product by ID. Returns 204 No Content even if the ID doesn't exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
