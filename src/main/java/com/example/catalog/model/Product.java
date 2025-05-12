package com.example.catalog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity // Maps this class to a database table
@Table(name = "products") // Table name will be "products"
@Getter // Lombok generates getters
@Setter // Lombok generates setters
@NoArgsConstructor // Lombok creates a no-argument constructor
@AllArgsConstructor // Lombok creates a constructor with all fields
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // hashCode() and equals() will use only fields with @EqualsAndHashCode.Include
public class Product {

    @Id // Primary key of the entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented ID by the database
    @EqualsAndHashCode.Include // ID is the only field considered for equals/hashCode
    private Long id;

    @NotBlank // Field must not be null or empty
    @Column(name = "name", nullable = false) // Column name is "name" and cannot be null
    private String name;

    @Size(max = 255) // Optional, but limits the length to 255 characters
    private String description;

    @NotNull // Cannot be null
    @DecimalMin(value = "0.00", inclusive = true) // Must be zero or greater
    @Column(name = "price", nullable = false, precision = 10, scale = 2) // Decimal config: max 10 digits, 2 after decimal
    private BigDecimal price;

    @Min(0) // Must be zero or greater
    private Integer stock;

    @CreationTimestamp // Automatically set when the entity is inserted
    @Column(name = "created_at", updatable = false) // Cannot be changed once set
    private LocalDateTime createdAt;

    @UpdateTimestamp // Automatically updated every time the entity is modified
    @Column(name = "updated_at") // No special restrictions
    private LocalDateTime updatedAt;

    @Column(name = "active")
    private boolean active = true;

    // Custom equals() to compare only by ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Same memory reference
        if (o == null || getClass() != o.getClass()) return false; // Different class or null
        Product product = (Product) o;
        return Objects.equals(id, product.id); // Only ID comparison
    }

    // Ensures hash-based collections (like HashMap/HashSet) work properly
    @Override
    public int hashCode() {
        return Objects.hash(id); // Generates hash from ID
    }
}