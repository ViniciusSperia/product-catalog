package com.example.catalog.module.product.service;

import com.example.catalog.exception.ResourceNotFoundException;
import com.example.catalog.module.product.dto.request.ProductFilterRequest;
import com.example.catalog.module.product.dto.request.ProductRequest;
import com.example.catalog.module.product.dto.response.ProductResponse;
import com.example.catalog.module.product.mapper.ProductMapper;
import com.example.catalog.module.product.model.Product;
import com.example.catalog.module.product.repository.ProductRepository;
import com.example.catalog.module.product.spec.ProductSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Page<ProductResponse> getAllActiveProducts(Pageable pageable) {
        return productRepository.findAll(ProductSpecification.filterBy(null, null, null), pageable)
                .map(productMapper::toResponse);
    }

    public Page<ProductResponse> filterActiveProducts(ProductFilterRequest filters, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.filterBy(
                filters.getName(),
                filters.getMinPrice(),
                filters.getMinStock()
        );
        return productRepository.findAll(spec, pageable)
                .map(productMapper::toResponse);
    }

    @Transactional
    public ProductResponse save(ProductRequest dto) {
        Product product = productMapper.toEntity(dto);
        product.setActive(true);
        Product saved = productRepository.save(product);
        log.info("Product created with ID: {}", saved.getId());
        return productMapper.toResponse(saved);
    }

    public ProductResponse findById(Long id) {
        Product product = findActiveOrThrow(id);
        return productMapper.toResponse(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = findActiveOrThrow(id);
        product.setActive(false);
        productRepository.save(product);
        log.info("Product soft-deleted with ID: {}", id);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest dto) {
        Product product = findActiveOrThrow(id);
        productMapper.updateEntity(product, dto);
        Product updated = productRepository.save(product);
        log.info("Product updated with ID: {}", id);
        return productMapper.toResponse(updated);
    }

    private Product findActiveOrThrow(Long id) {
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));
    }
}
