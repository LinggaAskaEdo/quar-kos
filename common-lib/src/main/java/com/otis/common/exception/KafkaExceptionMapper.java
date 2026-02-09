package com.otis.common.exception;

import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import com.otis.common.dto.ErrorResponse;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@RegisterForReflection
public class KafkaExceptionMapper {
	@ServerExceptionMapper
	public Response handleKafkaException(KafkaException exception) {
		String causeMessage = exception.getCause() != null ? exception.getCause().toString() : "Unknown cause";

		return Response
				.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse("An internal server error occurred", causeMessage))
				.build();
	}
}
