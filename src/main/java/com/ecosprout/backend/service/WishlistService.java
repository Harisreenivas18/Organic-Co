package com.ecosprout.backend.service;

import com.ecosprout.backend.model.Product;
import com.ecosprout.backend.model.User;
import com.ecosprout.backend.model.Wishlist;
import com.ecosprout.backend.model.WishlistId;
import com.ecosprout.backend.repository.ProductRepository;
import com.ecosprout.backend.repository.UserRepository;
import com.ecosprout.backend.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    // Get all products in a user's wishlist
    public List<Product> getWishlist(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<Wishlist> wishlistItems = wishlistRepository.findByUserId(user.getId());

        // Return just the list of products
        return wishlistItems.stream()
                .map(Wishlist::getProduct)
                .collect(Collectors.toList());
    }

    // Get just the IDs of wishlisted products (for the UI)
    public Set<Long> getWishlistProductIds(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return wishlistRepository.findProductIdsByUserId(user.getId());
    }

    /**
     * Toggles a product in the user's wishlist.
     * If it exists, remove it. If it doesn't, add it.
     * Returns true if the item is now in the wishlist, false if it was removed.
     */
    public boolean toggleWishlist(String username, Long productId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        WishlistId id = new WishlistId(user.getId(), product.getId());

        if (wishlistRepository.existsById(id)) {
            // Product is in wishlist, so remove it
            wishlistRepository.deleteById(id);
            return false; // isWishlisted = false
        } else {
            // Product is not in wishlist, so add it
            Wishlist newWishlistItem = new Wishlist();
            newWishlistItem.setId(id);
            newWishlistItem.setUser(user);
            newWishlistItem.setProduct(product);
            newWishlistItem.setCreatedAt(Instant.now());
            wishlistRepository.save(newWishlistItem);
            return true; // isWishlisted = true
        }
    }
}