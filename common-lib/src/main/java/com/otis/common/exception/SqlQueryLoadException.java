package com.otis.common.exception;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class SqlQueryLoadException extends RuntimeException {
	public SqlQueryLoadException(String message) {
		super(message);
	}

	public SqlQueryLoadException(String message, Throwable cause) {
		super(message, cause);
	}
}
