package com.multiservices.order_service.client;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Component
public class InventoryClient {

    private final RestTemplate rt = new RestTemplate();
    private final String base;

    public InventoryClient(@Value("${services.inventoryBaseUrl}") String base) {
        this.base = base;
    }

    public void reserve(String bearerToken, UUID orderId, UUID productId, int qty) {
        String url = base + "/inventory/reserve";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", bearerToken);

        Map<String, Object> body = Map.of(
                "orderId", orderId,
                "productId", productId,
                "qty", qty
        );

        ResponseEntity<String> res = rt.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
        if (!res.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("inventory reserve failed");
        }
    }

    public void confirm(String bearerToken, UUID orderId) {
        String url = base + "/inventory/confirm/" + orderId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", bearerToken);
        ResponseEntity<String> res = rt.exchange(url, HttpMethod.POST, new HttpEntity<>(headers), String.class);
        if (!res.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("inventory confirm failed");
        }
    }

    public void release(String bearerToken, UUID orderId) {
        String url = base + "/inventory/release/" + orderId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", bearerToken);
        rt.exchange(url, HttpMethod.POST, new HttpEntity<>(headers), String.class);
    }
}
