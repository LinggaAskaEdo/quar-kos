package com.otis.ordersvc.dto;

import java.math.BigDecimal;
import java.util.UUID;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record OrderItemDTO(
		UUID id,
		UUID orderId,
		UUID productId,
		String productName,
		String productDescription,
		Integer quantity,
		BigDecimal price) {
}
