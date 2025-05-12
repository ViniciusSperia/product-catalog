package com.example.catalog.service;

import com.example.catalog.dto.request.ProductRequest;
import com.example.catalog.dto.request.ProductFilterRequest;
import com.example.catalog.dto.response.ProductResponse;
import com.example.catalog.exception.ResourceNotFoundException;
import com.example.catalog.mapper.ProductMapper;
import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    /**
     * Returns all active products paginated and ordered.
     */
    public Page<ProductResponse> getAllActiveProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable)
                .map(productMapper::toResponse);
    }

    /**
     * Returns all filtered products with pagination and sorting.
     */
    public Page<ProductResponse> filterActiveProducts(ProductFilterRequest filters, Pageable pageable) {
        return productRepository.findAllByFilters(
                filters.getName(),
                filters.getMinPrice(),
                filters.getMinStock(),
                pageable
        ).map(productMapper::toResponse);
    }

    /**
     * Creates and saves a new product with 'active = true'.
     * Uses ProductMapper to convert DTO to Entity.
     */
    @Transactional
    public ProductResponse save(ProductRequest dto) {
        Product product = productMapper.toEntity(dto);
        log.info("Creating product: {}", dto.getName());
        product.setActive(true); // ensure it's active on creation
        Product saved = productRepository.save(product);
        log.info("Product created with ID: {}", saved.getId());
        return productMapper.toResponse(saved);
    }

    /**
     * Finds a product by ID if active, or throws exception.
     */
    public ProductResponse findById(Long id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));
        return productMapper.toResponse(product);
    }

    /**
     * Soft deletes a product by setting 'active = false'.
     */
    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));
        product.setActive(false);
        productRepository.save(product);
        log.info("Product soft-deleted with ID: {}", id);
    }

    /**
     * Updates an existing product by ID.
     */
    @Transactional
    public ProductResponse update(Long id, ProductRequest dto) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));

        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());

        Product updated = productRepository.save(product);
        log.info("Product updated with ID: {}", id);
        return productMapper.toResponse(updated);
    }
}
