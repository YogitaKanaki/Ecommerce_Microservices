package com.multiservices.inventory_service.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock_reservations",
        indexes = {
                @Index(name = "idx_res_order", columnList = "orderId", unique = true),
                @Index(name = "idx_res_status", columnList = "status"),
                @Index(name = "idx_res_user_email", columnList = "userEmail"),
                @Index(name = "idx_res_product", columnList = "productId")
        })
public class StockReservation {

    public enum Status { RESERVED, CONFIRMED, RELEASED }

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID productId;

    //  snapshot product name at reservation time
    @Column(nullable = false, length = 200)
    private String productName;

    //  who reserved (from JWT)
    @Column(nullable = false, length = 200)
    private String userEmail;

    @Column(nullable = false)
    private int qty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.RESERVED;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
}
