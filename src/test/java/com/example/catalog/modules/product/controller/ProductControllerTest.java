package com.example.catalog.modules.product.controller;

import com.example.catalog.module.product.controller.ProductController;
import com.example.catalog.module.product.dto.request.ProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.example.catalog.module.product.dto.response.ProductResponse;
import com.example.catalog.exception.ResourceNotFoundException;
import com.example.catalog.module.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;


    @Test
    void shouldReturnProductById() throws Exception {
        ProductResponse mockProduct = new ProductResponse();
        mockProduct.setId(1L);
        mockProduct.setName("Test Product");
        mockProduct.setPrice(new BigDecimal("10.00"));
        mockProduct.setStock(5);
        mockProduct.setCreatedAt(LocalDateTime.now());
        mockProduct.setUpdatedAt(LocalDateTime.now());

        when(productService.findById(1L)).thenReturn(mockProduct);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.price").isNumber())
                .andExpect(jsonPath("$.stock").isNumber())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void shouldReturn404WhenProductNotFound() throws Exception {
        when(productService.findById(anyLong())).thenThrow(new ResourceNotFoundException("Product with ID 999 not found"));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }
    @Test
    void shouldCreateProductSuccessfullyAndReturn201() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setDescription("Test Description");
        request.setPrice(new BigDecimal("99.99"));
        request.setStock(10);

        ProductResponse response = new ProductResponse();
        response.setId(42L);
        response.setName("Test Product");
        response.setPrice(new BigDecimal("99.99"));
        response.setStock(10);
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());

        when(productService.save(any())).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/products/42"))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void shouldReturn400WhenCreatingInvalidProduct() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName(""); // inválido
        request.setPrice(new BigDecimal("0")); // inválido se houver @Min
        request.setStock(-1); // inválido se houver @Positive

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
