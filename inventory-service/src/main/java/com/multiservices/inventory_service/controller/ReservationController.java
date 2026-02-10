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
