package com.example.catalog.module.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {

    @NotBlank
    private String name;

    private String slug; // pode ser null para gerar automaticamente

    @NotBlank
    private String iconUrl;

    private boolean active = true;
}
