package com.otis.common.exception;

import java.util.UUID;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class EntityNotFoundException extends RepositoryException {
	private final String entityName;
	private final UUID entityId;

	public EntityNotFoundException(String entityName, UUID entityId) {
		super(String.format("%s with id %s not found", entityName, entityId));
		this.entityName = entityName;
		this.entityId = entityId;
	}

	public EntityNotFoundException(String entityName, String fieldName, Object fieldValue) {
		super(String.format("%s with %s '%s' not found", entityName, fieldName,
				fieldValue));
		this.entityName = entityName;
		this.entityId = null;
	}

	public String getEntityName() {
		return entityName;
	}

	public UUID getEntityId() {
		return entityId;
	}
}
