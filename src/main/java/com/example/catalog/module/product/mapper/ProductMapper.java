
package com.example.catalog.module.product.mapper;

import com.example.catalog.module.category.dto.CategoryDTO;
import com.example.catalog.module.category.model.Category;
import com.example.catalog.module.product.dto.request.ProductRequest;
import com.example.catalog.module.product.dto.response.ProductResponse;
import com.example.catalog.module.product.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setImageUrl(product.getImageUrl());

        if (product.getCategory() != null) {
            Category category = product.getCategory();
            CategoryDTO categoryDTO = new CategoryDTO(
                    category.getId(),
                    category.getName(),
                    category.getSlug()
            );
            response.setCategory(categoryDTO);
        }

        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }

    public Product toEntity(ProductRequest dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImageUrl(dto.getImageUrl());

        Category category = new Category();
        category.setId(dto.getCategoryId());
        product.setCategory(category);

        return product;
    }

    public void updateEntity(Product product, ProductRequest dto) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImageUrl(dto.getImageUrl());

        Category category = new Category();
        category.setId(dto.getCategoryId());
        product.setCategory(category);
    }
}