package com.otis.ordersvc.config;

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
		com.otis.ordersvc.model.Order.class,
		com.otis.ordersvc.model.OrderItem.class,
		com.otis.ordersvc.model.Product.class,
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
		"com.otis.ordersvc.model.Order",
		"com.otis.ordersvc.model.OrderItem",
		"com.otis.ordersvc.model.Product"
})
public class ReflectionConfig {
	// This class is just for registering classes for native compilation
}
