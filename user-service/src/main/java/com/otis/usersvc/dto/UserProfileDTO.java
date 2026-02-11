package com.otis.usersvc.dto;

import java.util.UUID;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record UserProfileDTO(
		UUID id,
		UUID userId,
		String firstName,
		String lastName,
		String phone,
		String address) {
}
