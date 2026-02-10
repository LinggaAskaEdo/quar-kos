package com.otis.common.exception;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class CreationFailedException extends RepositoryException {
	public CreationFailedException(String entityName) {
		super(String.format("Failed to create %s", entityName));
	}

	public CreationFailedException(String entityName, Throwable cause) {
		super(String.format("Failed to create %s", entityName), cause);
	}
}
