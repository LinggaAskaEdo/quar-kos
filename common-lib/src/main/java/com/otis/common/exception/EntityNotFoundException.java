package com.otis.common.exception;

public class EntityNotFoundException extends RepositoryException {
    private final String entityName;
    private final Long entityId;

    public EntityNotFoundException(String entityName, Long entityId) {
        super(String.format("%s with id %d not found", entityName, entityId));
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

    public Long getEntityId() {
        return entityId;
    }
}
