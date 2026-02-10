package com.otis.common.exception;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class RepositoryException extends RuntimeException {
	public RepositoryException(String message) {
		super(message);
	}

	public RepositoryException(String message, Throwable cause) {
		super(message, cause);
	}
}
