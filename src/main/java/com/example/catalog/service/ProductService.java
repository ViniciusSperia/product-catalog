package com.example.catalog.service;

import com.example.catalog.dto.request.ProductRequest;
import com.example.catalog.dto.response.ProductResponse;
import com.example.catalog.mapper.ProductMapper;
import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // Automatically generates constructor for final fields
@Slf4j // Enables logging with log.info, log.error, etc.
public class ProductService {

    private final ProductRepository repository;

    /**
     * Fetches all active products from the database.
     */
    public List<ProductResponse> findAll() {
        log.info("Fetching all active products");
        return repository.findByActiveTrue().stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    /**
     * Fetches a product by ID if it's active.
     * Throws EntityNotFoundException if not found.
     */
    public ProductResponse findById(Long id) {
        log.info("Fetching product by id: {}", id);
        Product product = repository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", id);
                    return new EntityNotFoundException("Product not found");
                });

        return ProductMapper.toResponse(product);
    }

    /**
     * Creates and saves a new product with `active = true`.
     * Uses ProductMapper to convert DTO to Entity.
     */
    @Transactional
    public ProductResponse save(ProductRequest dto) {
        Product product = ProductMapper.toEntity(dto);
        log.info("Creating product: {}", dto.getName());
        product.setActive(true); // ensure it's active on creation
        Product saved = repository.save(product);
        log.info("Product created with ID: {}", saved.getId());
        return ProductMapper.toResponse(saved);
    }

    /**
     * Updates an existing product by ID.
     * Throws EntityNotFoundException if not found or inactive.
     */
    @Transactional
    public ProductResponse update(Long id, ProductRequest dto) {
        log.info("Updating product id: {}", id, dto);
        Product product = repository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> {
                    log.warn("Product not found or inactive: {}", id);
                    return new EntityNotFoundException("Product not found");
                });

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());

        Product updated = repository.save(product);
        log.info("Product updated successfully: {}", updated.getId());
        return ProductMapper.toResponse(updated);
    }

    /**
     * Performs a soft delete by marking the product as inactive.
     * Throws EntityNotFoundException if not found or already inactive.
     */
    @Transactional
    public void delete(Long id) {
        log.info("Soft-deleting product id: {}", id);
        Product product = repository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> {
                    log.warn("Product not found or already inactive: {}", id);
                    return new EntityNotFoundException("Product not found");
                });

        product.setActive(false);
        repository.save(product);
        log.info("Product marked as inactive: {}", id);
    }
}
