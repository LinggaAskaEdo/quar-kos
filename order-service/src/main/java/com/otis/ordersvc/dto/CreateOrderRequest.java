package com.otis.ordersvc.dto;

import java.util.List;
import java.util.UUID;

public class CreateOrderRequest {
    private UUID userId;
    private String username;
    private List<OrderItemRequest> items;

    public UUID getUserId() {
        return this.userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<OrderItemRequest> getItems() {
        return this.items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}
