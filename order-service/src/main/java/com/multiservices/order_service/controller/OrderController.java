package com.multiservices.order_service.controller;


import com.multiservices.order_service.dto.OrderDtos;
import com.multiservices.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService svc;

    public OrderController(OrderService svc) { this.svc = svc; }

    @PostMapping
    public ResponseEntity<OrderDtos.OrderRes> create(
            @Valid @RequestBody OrderDtos.CreateOrderReq req,
            Authentication auth,
            @RequestHeader("Authorization") String bearer
    ) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        var o = svc.place(userId, bearer, req.productId(), req.qty());
        return ResponseEntity.ok(new OrderDtos.OrderRes(o.getId(), o.getProductId(), o.getQty(), o.getStatus().name()));
    }

    @GetMapping("/me")
    public ResponseEntity<?> myOrders(Authentication auth) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        return ResponseEntity.ok(svc.myOrders(userId));
    }
}

