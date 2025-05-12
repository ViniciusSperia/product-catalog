package com.example.catalog.repository;

import com.example.catalog.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByActiveTrue();
    Optional<Product> findByIdAndActiveTrue(Long id);
    Page<Product> findByActiveTrue(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true "
            + "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) "
            + "AND (:minPrice IS NULL OR p.price >= :minPrice) "
            + "AND (:minStock IS NULL OR p.stock >= :minStock)")
    Page<Product> findAllByFilters(
            @Param("name") String name,
            @Param("minPrice") Double minPrice,
            @Param("minStock") Integer minStock,
            Pageable pageable
    );
}

