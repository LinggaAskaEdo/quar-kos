package com.otis.usersvc.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record RoleDTO(
		UUID id,
		String name,
		String description,
		LocalDateTime assignedAt) {
}
