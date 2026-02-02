package com.multiservices.order_service.repo;


import com.multiservices.order_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepo extends JpaRepository<Order, UUID> {
    List<Order> findByUserId(UUID userId);
}
