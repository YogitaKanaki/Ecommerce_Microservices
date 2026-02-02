package com.multiservices.inventory_service.repo;




import com.multiservices.inventory_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepo extends JpaRepository<Product, UUID> {
    Optional<Product> findBySku(String sku);
}

