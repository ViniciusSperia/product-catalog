package com.example.catalog.module.category.mapper;

import com.example.catalog.module.category.dto.request.CategoryRequest;
import com.example.catalog.module.category.dto.response.CategoryResponse;
import com.example.catalog.module.category.model.Category;
import org.springframework.stereotype.Component;

import java.text.Normalizer;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request) {
        return Category.builder()
                .name(request.getName())
                .slug(generateSlug(request.getSlug(), request.getName()))
                .iconUrl(request.getIconUrl())
                .active(request.isActive())
                .build();
    }

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getIconUrl(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }

    public static String generateSlug(String slug, String nameFallback) {
        String base = (slug == null || slug.isBlank()) ? nameFallback : slug;
        return Normalizer.normalize(base, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("[^a-zA-Z0-9]+", "-")
                .toLowerCase()
                .replaceAll("^-|-$", "");
    }
}
