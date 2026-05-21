package com.ecosprout.backend.controller;

import com.ecosprout.backend.model.Order;
import com.ecosprout.backend.model.User;
import com.ecosprout.backend.repository.OrderRepository;
import com.ecosprout.backend.repository.UserRepository;
import com.ecosprout.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    public ResponseEntity<Order> getCart(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Optional<Order> cart = orderRepository.findByUserAndStatus(user, "In Cart");

        if (cart.isPresent() && !cart.get().getOrderItems().isEmpty()) {
            return ResponseEntity.ok(cart.get());
        } else {

            return ResponseEntity.noContent().build();
        }
    }


    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> payload, Principal principal) {
        try {
            String username = principal.getName();
            Long productId = ((Number) payload.get("productId")).longValue();

            int quantity = 1; // Default
            if (payload.containsKey("quantity")) {
                quantity = ((Number) payload.get("quantity")).intValue();
            }

            Order updatedCart = cartService.addProductToCart(username, productId, quantity);
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }


    @PostMapping("/remove")
    public ResponseEntity<Order> removeFromCart(@RequestBody Map<String, Object> payload, Principal principal) {
        try {
            String username = principal.getName();
            Long productId = ((Number) payload.get("productId")).longValue();

            Order updatedCart = cartService.removeProductFromCart(username, productId);
            return ResponseEntity.ok(updatedCart);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateCartQuantity(@RequestBody Map<String, Object> payload, Principal principal) {
        try {
            String username = principal.getName();
            Long productId = ((Number) payload.get("productId")).longValue();
            int newQuantity = ((Number) payload.get("quantity")).intValue();

            Order updatedCart = cartService.updateCartItemQuantity(username, productId, newQuantity);
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }


    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody Map<String, String> shippingDetails, Principal principal) {
        try {
            String username = principal.getName();
            Order completedOrder = cartService.checkoutCart(username, shippingDetails);
            return ResponseEntity.ok(completedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }
}