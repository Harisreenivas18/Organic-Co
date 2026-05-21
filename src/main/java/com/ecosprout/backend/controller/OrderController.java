package com.ecosprout.backend.controller;

import com.ecosprout.backend.model.Order;
import com.ecosprout.backend.model.User;
import com.ecosprout.backend.repository.OrderRepository;
import com.ecosprout.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;


    @GetMapping
    public ResponseEntity<List<Order>> getOrderHistory(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<String> completedStatuses = Arrays.asList("Processing", "Shipped", "Delivered");

        List<Order> orders = orderRepository.findByUserAndStatusInOrderByOrderDateDesc(user, completedStatuses);

        return ResponseEntity.ok(orders);
    }
}