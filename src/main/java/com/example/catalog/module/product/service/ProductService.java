package com.example.catalog.module.product.service;

import com.example.catalog.exception.ResourceNotFoundException;
import com.example.catalog.module.product.dto.request.ProductFilterRequest;
import com.example.catalog.module.product.dto.request.ProductRequest;
import com.example.catalog.module.product.dto.response.ProductResponse;
import com.example.catalog.module.product.dto.response.ProductSummaryDto;
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

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Page<ProductResponse> getAllActiveProducts(Pageable pageable) {
        ProductFilterRequest emptyFilter = ProductFilterRequest.of(null, null, null, null, null);
        Specification<Product> spec = ProductSpecification.filterBy(emptyFilter);
        return productRepository.findAll(spec, pageable)
                .map(productMapper::toResponse);
    }

    public Page<ProductResponse> filterActiveProducts(ProductFilterRequest filters, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.filterBy(filters);
        return productRepository.findAll(spec, pageable)
                .map(productMapper::toResponse);
    }

    @Transactional
    public ProductResponse save(ProductRequest dto) {
        UUID id = UUID.randomUUID();

        String baseSlug = dto.getName()
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        String fullSlug = baseSlug + "-" + id.toString().substring(0, 6);

        Product product = productMapper.toEntity(dto);
        product.setId(id);
        product.setSlug(fullSlug);
        product.setActive(true);

        Product saved = productRepository.save(product);
        log.info("Product created with ID: {}, slug: {}", saved.getId(), saved.getSlug());

        return productMapper.toResponse(saved);
    }

    public ProductResponse findById(UUID id) {
        Product product = findActiveOrThrow(id);
        return productMapper.toResponse(product);
    }

    @Transactional
    public void delete(UUID id) {
        Product product = findActiveOrThrow(id);
        product.setActive(false);
        productRepository.save(product);
        log.info("Product soft-deleted with ID: {}", id);
    }

    @Transactional
    public ProductResponse update(UUID id, ProductRequest dto) {
        Product product = findActiveOrThrow(id);
        productMapper.updateEntity(product, dto);
        Product updated = productRepository.save(product);
        log.info("Product updated with ID: {}", id);
        return productMapper.toResponse(updated);
    }

    public Page<ProductSummaryDto> findPublicProducts(ProductFilterRequest filters, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.filterBy(filters);
        return productRepository.findAll(spec, pageable)
                .map(product -> new ProductSummaryDto(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getImageUrl(),
                        product.getCategory().getName(),
                        product.getSlug()
                ));
    }

    public ProductResponse findPublicBySlug(String slug) {
        Product product = productRepository.findBySlugAndActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return productMapper.toResponse(product);
    }

    private Product findActiveOrThrow(UUID id) {
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));
    }
}
