package com.example.catalog;

import com.example.catalog.model.Product;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

@ActiveProfiles("test")
@SpringBootTest
public class ProductTestFactory {

    public static Product create(String name, BigDecimal price, int stock) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);
        product.setActive(true);
        return product;
    }

    public static Product createDefault() {
        return create("Default Product", new BigDecimal("10.00"), 5);
    }
}
