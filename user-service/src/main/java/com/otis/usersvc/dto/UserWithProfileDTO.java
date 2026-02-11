package com.otis.usersvc.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record UserWithProfileDTO(
		UUID userId,
		String username,
		String email,
		LocalDateTime createdAt,
		UserProfileDTO profile) {
}
