package com.multiservices.inventory_service.controller;

import com.multiservices.inventory_service.dto.InventoryDtos;
import com.multiservices.inventory_service.repo.StockReservationRepo;
import com.multiservices.inventory_service.service.InventoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/inventory")
public class ReservationController {

    private final InventoryService svc;
    private final StockReservationRepo reservationRepo;

    public ReservationController(InventoryService svc, StockReservationRepo reservationRepo) {
        this.svc = svc;
        this.reservationRepo = reservationRepo;
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserve(@Valid @RequestBody InventoryDtos.ReserveReq req,
                                     HttpServletRequest request) {

        String email = (String) request.getAttribute("userEmail");
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(401).body("missing user email in token");
        }

        var r = svc.reserve(email, req.orderId(), req.productId(), req.qty());
        return ResponseEntity.ok(new InventoryDtos.ReserveRes(r.getId(), r.getStatus().name()));
    }

    @PostMapping("/confirm/{orderId}")
    public ResponseEntity<?> confirm(@PathVariable UUID orderId) {
        var r = svc.confirm(orderId);
        return ResponseEntity.ok(Map.of("status", r.getStatus().name()));
    }

    @PostMapping("/release/{orderId}")
    public ResponseEntity<?> release(@PathVariable UUID orderId) {
        var r = svc.release(orderId);
        return ResponseEntity.ok(Map.of("status", r.getStatus().name()));
    }

    //  ADMIN ONLY (secured in SecurityConfig)
    @GetMapping("/reservations")
    public ResponseEntity<?> allReservations() {
        return ResponseEntity.ok(reservationRepo.findAll());
    }

    // ADMIN ONLY (secured in SecurityConfig)
    @GetMapping("/reservations/by-user")
    public ResponseEntity<?> byUser(@RequestParam String email) {
        return ResponseEntity.ok(reservationRepo.findAllByUserEmail(email.toLowerCase()));
    }
}
