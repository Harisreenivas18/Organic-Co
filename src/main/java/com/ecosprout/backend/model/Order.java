package com.ecosprout.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- THIS IS THE FIX ---
    // This side of the relationship ("many" orders to "one" user)
    // gets @JsonBackReference to break the infinite loop.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
    // --- END FIX ---

    @Column(name = "order_date")
    private Instant orderDate;

    // --- THIS IS THE FIX ---
    // This annotation now correctly points to "total_price"
    // which matches your database schema.
    @Column(name = "total_price", nullable = false)
    private BigDecimal totalAmount;
    // --- END FIX ---

    @Column(nullable = false)
    private String status;

    @Column(name = "shipping_address_line1")
    private String shippingAddressLine1;

    @Column(name = "shipping_address_line2")
    private String shippingAddressLine2;

    @Column(name = "shipping_city")
    private String shippingCity;

    @Column(name = "shipping_postal_code")
    private String shippingPostalCode;

    @Column(name = "shipping_country")
    private String shippingCountry;

    // This relationship was also part of the infinite loop fix.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<OrderItem> orderItems = new HashSet<>();
}