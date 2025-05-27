package com.example.catalog.module.category.controller;

import com.example.catalog.module.category.dto.request.CategoryRequest;
import com.example.catalog.module.category.dto.response.CategoryResponse;
import com.example.catalog.module.category.mapper.CategoryMapper;
import com.example.catalog.module.category.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Endpoints for managing product categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/public")
    public ResponseEntity<List<CategoryResponse>> getPublicCategories() {
        return ResponseEntity.ok(categoryService.findAllPublic());
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.create(request);
        return ResponseEntity.created(URI.create("/api/categories/" + response.getId())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(new CategoryMapper().toResponse(categoryService.getById(id)));
    }
}
