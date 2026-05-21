package com.ecosprout.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    // --- THIS IS THE FIX ---
    // The "mappedBy" value must match the field name in the Product class.
    // In Product.java, the field is "private Set<Category> categories;"
    @ManyToMany(mappedBy = "categories") // <-- This MUST say "categories"
    @JsonBackReference // <-- REPLACE @JsonIgnore WITH THIS
    private Set<Product> products;
    // --- END FIX ---
}