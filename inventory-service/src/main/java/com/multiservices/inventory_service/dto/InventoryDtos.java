package com.multiservices.inventory_service.dto;



import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class InventoryDtos {

    public record CreateProductReq(
            @NotBlank String sku,
            @NotBlank String name,
            @Min(0) int availableQty
    ) {}

    public record ReserveReq(
            @NotNull UUID orderId,
            @NotNull UUID productId,
            @Min(1) int qty
    ) {}

    public record ReserveRes(
            UUID reservationId,
            String status
    ) {}
}

