package com.example.catalog.module.product.repository;

import com.example.catalog.module.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByIdAndActiveTrue(UUID id);
    Page<Product> findByActiveTrue(Pageable pageable);
    Optional<Product> findBySlugAndActiveTrue(String slug);
    Optional<Product> findById(UUID id);
}

