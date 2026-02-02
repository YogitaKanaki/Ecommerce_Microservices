package com.multiservices.inventory_service.controller;


import com.multiservices.inventory_service.dto.InventoryDtos;
import com.multiservices.inventory_service.model.Product;
import com.multiservices.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final InventoryService svc;

    public ProductController(InventoryService svc) { this.svc = svc; }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody InventoryDtos.CreateProductReq req) {
        return ResponseEntity.ok(svc.createProduct(req.sku(), req.name(), req.availableQty()));
    }
}

