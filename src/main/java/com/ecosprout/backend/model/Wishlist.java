package com.ecosprout.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "wishlist")
@Getter
@Setter
public class Wishlist {

    @EmbeddedId
    private WishlistId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId") // This maps the userId part of the @EmbeddedId
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne(fetch = FetchType.EAGER) // We want product info
    @MapsId("productId") // This maps the productId part of the @EmbeddedId
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "created_at")
    private Instant createdAt;
}