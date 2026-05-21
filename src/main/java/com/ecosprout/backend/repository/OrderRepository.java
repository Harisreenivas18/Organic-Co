package com.ecosprout.backend.repository;

import com.ecosprout.backend.model.Order;
import com.ecosprout.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByUserAndStatus(User user, String status);

    List<Order> findByUserAndStatusInOrderByOrderDateDesc(User user, List<String> statuses);
}