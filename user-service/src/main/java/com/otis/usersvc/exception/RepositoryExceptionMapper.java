package com.otis.usersvc.exception;

import com.otis.usersvc.dto.ErrorResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RepositoryExceptionMapper implements ExceptionMapper<RepositoryException> {
    @Override
    public Response toResponse(RepositoryException exception) {
        if (exception instanceof EntityNotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(exception.getMessage()))
                    .build();
        } else if (exception instanceof CreationFailedException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(exception.getMessage()))
                    .build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("An internal server error occurred"))
                    .build();
        }
    }
}
