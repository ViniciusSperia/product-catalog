package com.example.catalog.service;

import com.example.catalog.model.dto.ProductDto;
import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ProductService {

    private final ProductRepository repository;

    @Autowired // Injects the repository dependency
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns all products from the database.
     */
    public List<Product> findAll() {
        return repository.findAll();
    }

    /**
     * Finds a product by ID.
     * Returns an Optional, which allows the caller to handle missing data.
     */
    public Optional<Product> findById(Long id) {
        return repository.findById(id);
    }

    /**
     * Saves a new product.
     * @Transactional ensures that DB operations complete together or rollback on failure.
     */
    @Transactional
    public Product save(ProductDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());

        return repository.save(product);
    }

    /**
     * Updates an existing product.
     * Returns null if the product doesn't exist.
     */
    @Transactional
    public Product update(Long id, ProductDto dto) {
        Optional<Product> existing = repository.findById(id);
        if (existing.isEmpty()) {
            return null;
        }

        Product product = existing.get();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());

        return repository.save(product);
    }

    /**
     * Deletes a product by ID.
     * If the product doesn't exist, does nothing.
     */
    @Transactional
    public void delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        }
    }
}
