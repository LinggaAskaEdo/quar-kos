package com.otis.common.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ErrorResponse {
	private final String message;
	private final String cause;
	private final long timestamp;

	public ErrorResponse(String message, String cause) {
		this.message = message;
		this.cause = cause;
		this.timestamp = System.currentTimeMillis();
	}

	public String getMessage() {
		return message;
	}

	public String getCause() {
		return cause;
	}

	public long getTimestamp() {
		return timestamp;
	}
}
