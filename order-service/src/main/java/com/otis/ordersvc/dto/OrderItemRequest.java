package com.otis.ordersvc.dto;

import java.util.UUID;

public class OrderItemRequest {
    private UUID productId;
    private Integer quantity;

    public UUID getProductId() {
        return this.productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
