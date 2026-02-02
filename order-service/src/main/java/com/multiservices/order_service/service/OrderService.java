package com.multiservices.order_service.service;


import com.multiservices.order_service.client.InventoryClient;
import com.multiservices.order_service.model.Order;
import com.multiservices.order_service.repo.OrderRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepo repo;
    private final InventoryClient inventory;

    public OrderService(OrderRepo repo, InventoryClient inventory) {
        this.repo = repo;
        this.inventory = inventory;
    }

    @Transactional
    public Order place(UUID userId, String bearerToken, UUID productId, int qty) {
        Order o = new Order();
        o.setUserId(userId);
        o.setProductId(productId);
        o.setQty(qty);
        o.setStatus(Order.Status.CREATED);

        o = repo.save(o);

        try {
            inventory.reserve(bearerToken, o.getId(), productId, qty);
            inventory.confirm(bearerToken, o.getId());
            return o;
        } catch (Exception ex) {
            // rollback inventory reservation
            inventory.release(bearerToken, o.getId());
            o.setStatus(Order.Status.CANCELLED);
            return repo.save(o);
        }
    }

    public List<Order> myOrders(UUID userId) {
        return repo.findByUserId(userId);
    }
}
