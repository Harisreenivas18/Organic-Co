package com.ecosprout.backend.controller;

import com.ecosprout.backend.model.Product;
import com.ecosprout.backend.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin(origins = "*")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;


    @GetMapping
    public ResponseEntity<List<Product>> getWishlist(Principal principal) {
        List<Product> wishlist = wishlistService.getWishlist(principal.getName());
        return ResponseEntity.ok(wishlist);
    }


    @GetMapping("/ids")
    public ResponseEntity<Set<Long>> getWishlistIds(Principal principal) {
        Set<Long> ids = wishlistService.getWishlistProductIds(principal.getName());
        return ResponseEntity.ok(ids);
    }

    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Boolean>> toggleWishlist(@RequestBody Map<String, Object> payload, Principal principal) {
        Long productId = ((Number) payload.get("productId")).longValue();
        boolean isWishlisted = wishlistService.toggleWishlist(principal.getName(), productId);

        return ResponseEntity.ok(Map.of("isWishlisted", isWishlisted));
    }
}