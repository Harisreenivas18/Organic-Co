package com.ecosprout.backend.repository;

import com.ecosprout.backend.model.Wishlist;
import com.ecosprout.backend.model.WishlistId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, WishlistId> {

    // Find all wishlist items for a specific user
    List<Wishlist> findByUserId(Long userId);

    // Find just the product IDs for a specific user
    @Query("SELECT w.product.id FROM Wishlist w WHERE w.user.id = :userId")
    Set<Long> findProductIdsByUserId(Long userId);

    // Check if an item already exists in the wishlist
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    // Delete an item from the wishlist
    void deleteByUserIdAndProductId(Long userId, Long productId);
}