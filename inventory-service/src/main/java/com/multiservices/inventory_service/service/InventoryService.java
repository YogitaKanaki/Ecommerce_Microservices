package com.multiservices.inventory_service.service;

import com.multiservices.inventory_service.model.Product;
import com.multiservices.inventory_service.model.StockReservation;
import com.multiservices.inventory_service.repo.ProductRepo;
import com.multiservices.inventory_service.repo.StockReservationRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"
                ));

        if (p.getAvailableQty() < qty) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Insufficient stock"
            );
        }

        reservationRepo.findByOrderId(orderId).ifPresent(r -> {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Reservation already exists for order"
            );
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
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reservation not found"
                ));

        if (r.getStatus() == StockReservation.Status.CONFIRMED) {
            return r;
        }

        if (r.getStatus() == StockReservation.Status.RELEASED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot confirm a released reservation"
            );
        }

        r.setStatus(StockReservation.Status.CONFIRMED);
        return reservationRepo.save(r);
    }

    @Transactional
    public StockReservation release(UUID orderId) {

        StockReservation r = reservationRepo.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reservation not found"
                ));

        if (r.getStatus() == StockReservation.Status.RELEASED) {
            return r;
        }

        if (r.getStatus() == StockReservation.Status.CONFIRMED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot release a confirmed reservation"
            );
        }

        Product p = productRepo.findById(r.getProductId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"
                ));

        p.setAvailableQty(p.getAvailableQty() + r.getQty());
        r.setStatus(StockReservation.Status.RELEASED);

        productRepo.save(p);
        return reservationRepo.save(r);
    }
}
