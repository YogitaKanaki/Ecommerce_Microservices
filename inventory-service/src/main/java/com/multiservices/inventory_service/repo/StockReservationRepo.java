package com.multiservices.inventory_service.repo;

import com.multiservices.inventory_service.model.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockReservationRepo extends JpaRepository<StockReservation, UUID> {
    Optional<StockReservation> findByOrderId(UUID orderId);
    List<StockReservation> findAllByUserEmail(String userEmail);
}
