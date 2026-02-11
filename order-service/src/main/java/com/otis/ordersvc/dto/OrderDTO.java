package com.otis.ordersvc.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record OrderDTO(
		UUID id,
		UUID userId,
		String username,
		BigDecimal totalAmount,
		String status,
		UUID correlationId,
		LocalDateTime createdAt,
		List<OrderItemDTO> items) {
}
