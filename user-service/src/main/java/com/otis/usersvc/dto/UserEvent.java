package com.otis.usersvc.dto;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserEvent(
		UUID correlationId,
		String event,
		UUID userId,
		String username,
		String firstName,
		String lastName,
		String email,
		Instant timestamp) {
	// Factory method for USER_CREATED event
	public static UserEvent createdUser(UUID correlationId, UUID userId, String username, String email) {
		return new UserEvent(
				correlationId,
				"USER_CREATED",
				userId,
				username,
				null, // firstName
				null, // lastName
				email,
				Instant.now());
	}

	// Factory method for USER_PROFILE_CREATED event
	public static UserEvent createdUserProfile(UUID correlationId, UUID userId, String firstName, String lastName) {
		return new UserEvent(
				correlationId,
				"USER_PROFILE_CREATED",
				userId,
				null, // username
				firstName,
				lastName,
				null, // email
				Instant.now());
	}
}
