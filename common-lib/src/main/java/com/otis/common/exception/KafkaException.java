package com.otis.common.exception;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class KafkaException extends RuntimeException {
	public KafkaException(String message) {
		super(message);
	}

	public KafkaException(String message, Throwable cause) {
		super(message, cause);
	}
}
