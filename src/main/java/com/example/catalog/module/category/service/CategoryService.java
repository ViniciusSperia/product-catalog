package com.example.catalog.module.category.service;

import com.example.catalog.module.category.dto.request.CategoryRequest;
import com.example.catalog.module.category.dto.response.CategoryResponse;
import com.example.catalog.module.category.mapper.CategoryMapper;
import com.example.catalog.module.category.model.Category;
import com.example.catalog.module.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    public List<CategoryResponse> findAllPublic() {
        return repository.findAllByActiveTrue().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<CategoryResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public CategoryResponse create(CategoryRequest request) {
        Category saved = repository.save(mapper.toEntity(request));
        return mapper.toResponse(saved);
    }

    public Category getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }
}
