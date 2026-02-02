package com.multiservices.inventory_service.service;


import com.multiservices.inventory_service.model.Product;
import com.multiservices.inventory_service.model.StockReservation;
import com.multiservices.inventory_service.repo.ProductRepo;
import com.multiservices.inventory_service.repo.StockReservationRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class InventoryService {

    private final ProductRepo productRepo;
    private final StockReservationRepo reservationRepo;

    public InventoryService(ProductRepo productRepo, StockReservationRepo reservationRepo) {
        this.productRepo = productRepo;
        this.reservationRepo = reservationRepo;
    }

    public Product createProduct(String sku, String name, int qty) {
        Product p = new Product();
        p.setSku(sku);
        p.setName(name);
        p.setAvailableQty(qty);
        return productRepo.save(p);
    }

    @Transactional
    public StockReservation reserve(UUID orderId, UUID productId, int qty) {
        Product p = productRepo.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("product not found"));

        if (p.getAvailableQty() < qty) {
            throw new IllegalArgumentException("insufficient stock");
        }

        // prevent duplicate reservation per order
        reservationRepo.findByOrderId(orderId).ifPresent(r -> {
            throw new IllegalArgumentException("reservation already exists for order");
        });

        p.setAvailableQty(p.getAvailableQty() - qty);

        StockReservation r = new StockReservation();
        r.setOrderId(orderId);
        r.setProductId(productId);
        r.setQty(qty);
        r.setStatus(StockReservation.Status.RESERVED);

        productRepo.save(p);
        return reservationRepo.save(r);
    }

    @Transactional
    public StockReservation confirm(UUID orderId) {
        StockReservation r = reservationRepo.findByOrderId(orderId)
                .orElseThrow(() -> new NoSuchElementException("reservation not found"));

        if (r.getStatus() == StockReservation.Status.CONFIRMED) return r;
        if (r.getStatus() == StockReservation.Status.RELEASED) {
            throw new IllegalArgumentException("cannot confirm released reservation");
        }

        r.setStatus(StockReservation.Status.CONFIRMED);
        return reservationRepo.save(r);
    }

    @Transactional
    public StockReservation release(UUID orderId) {
        StockReservation r = reservationRepo.findByOrderId(orderId)
                .orElseThrow(() -> new NoSuchElementException("reservation not found"));

        if (r.getStatus() == StockReservation.Status.RELEASED) return r;
        if (r.getStatus() == StockReservation.Status.CONFIRMED) {
            throw new IllegalArgumentException("cannot release confirmed reservation");
        }

        Product p = productRepo.findById(r.getProductId())
                .orElseThrow(() -> new NoSuchElementException("product not found"));

        p.setAvailableQty(p.getAvailableQty() + r.getQty());
        r.setStatus(StockReservation.Status.RELEASED);

        productRepo.save(p);
        return reservationRepo.save(r);
    }
}
