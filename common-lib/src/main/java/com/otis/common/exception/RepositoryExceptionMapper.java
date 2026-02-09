package com.otis.common.exception;

import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import com.otis.common.dto.ErrorResponse;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@RegisterForReflection
public class RepositoryExceptionMapper {
	@ServerExceptionMapper
	public Response handleRepositoryException(RepositoryException exception) {
		String causeMessage = exception.getCause() != null ? exception.getCause().toString() : "Unknown cause";

		if (exception instanceof EntityNotFoundException) {
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new ErrorResponse(exception.getMessage(), causeMessage))
					.build();
		} else if (exception instanceof CreationFailedException) {
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(new com.otis.common.dto.ErrorResponse(exception.getMessage(), causeMessage))
					.build();
		} else {
			return Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ErrorResponse("An internal server error occurred", causeMessage))
					.build();
		}
	}
}
