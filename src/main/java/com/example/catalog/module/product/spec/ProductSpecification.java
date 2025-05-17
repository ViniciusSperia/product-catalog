package com.example.catalog.module.product.spec;

import com.example.catalog.module.product.model.Product;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProductSpecification {

    public static Specification<Product> filterBy(String name, Double minPrice, Integer minStock) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isTrue(root.get("active")));

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("name").as(String.class)),
                        "%" + name.toLowerCase() + "%"
                ));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (minStock != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("stock"), minStock));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
