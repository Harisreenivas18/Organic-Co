package com.ecosprout.backend.model;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class WishlistId implements Serializable {

    private Long userId;
    private Long productId;

    // Constructors, equals, and hashCode are needed for composite keys

    public WishlistId() {}

    public WishlistId(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }
}