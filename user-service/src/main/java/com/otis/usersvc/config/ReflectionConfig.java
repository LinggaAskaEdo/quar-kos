package com.otis.usersvc.config;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(targets = {
		com.otis.common.exception.RepositoryExceptionMapper.class,
		com.otis.common.exception.RepositoryException.class,
		com.otis.common.exception.CreationFailedException.class,
		com.otis.common.exception.DataAccessException.class,
		com.otis.common.exception.EntityNotFoundException.class,
		com.otis.common.exception.KafkaExceptionMapper.class,
		com.otis.common.exception.KafkaException.class,
		com.otis.common.dto.ErrorResponse.class,
		com.otis.common.util.SqlQueryLoader.class,
		com.otis.common.exception.SqlQueryLoadException.class,
		com.otis.usersvc.model.Role.class,
		com.otis.usersvc.model.User.class,
		com.otis.usersvc.model.UserProfile.class,
		com.otis.usersvc.model.UserWithProfile.class,
		jakarta.ws.rs.core.Response.class,
		jakarta.ws.rs.core.Response.StatusType.class,
		jakarta.ws.rs.core.Response.Status.class
}, classNames = {
		"com.otis.common.exception.DataAccessException",
		"com.otis.common.exception.RepositoryException",
		"com.otis.common.exception.EntityNotFoundException",
		"com.otis.common.exception.CreationFailedException",
		"com.otis.common.exception.KafkaException",
		"com.otis.common.util.SqlQueryLoader",
		"com.otis.usersvc.model.Role",
		"com.otis.usersvc.model.User",
		"com.otis.usersvc.model.UserProfile",
		"com.otis.usersvc.model.UserWithProfile"
})
public class ReflectionConfig {
	// This class is just for registering classes for native compilation
}
