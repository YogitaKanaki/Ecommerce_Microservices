package com.multiservices.inventory_service.controller;


import com.multiservices.inventory_service.dto.InventoryDtos;
import com.multiservices.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/inventory")
public class ReservationController {

    private final InventoryService svc;

    public ReservationController(InventoryService svc) { this.svc = svc; }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserve(@Valid @RequestBody InventoryDtos.ReserveReq req) {
        var r = svc.reserve(req.orderId(), req.productId(), req.qty());
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
}
