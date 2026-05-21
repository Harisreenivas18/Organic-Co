package com.ecosprout.backend.repository;

import com.ecosprout.backend.model.Order;
import com.ecosprout.backend.model.OrderItem;
import com.ecosprout.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    Optional<OrderItem> findByOrderAndProduct(Order order, Product product);
}