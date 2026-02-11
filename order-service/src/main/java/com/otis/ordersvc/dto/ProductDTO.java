package com.otis.ordersvc.dto;

import java.math.BigDecimal;
import java.util.UUID;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record ProductDTO(
		UUID id,
		String name,
		String description,
		BigDecimal price,
		Integer stock) {
}
