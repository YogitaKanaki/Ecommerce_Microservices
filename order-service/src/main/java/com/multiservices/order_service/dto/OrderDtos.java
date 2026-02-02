package com.multiservices.order_service.dto;



import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class OrderDtos {
    public record CreateOrderReq(@NotNull UUID productId, @Min(1) int qty) {}
    public record OrderRes(UUID id, UUID productId, int qty, String status) {}
}

