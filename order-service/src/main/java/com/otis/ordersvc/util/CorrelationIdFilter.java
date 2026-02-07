package com.otis.ordersvc.util;

import java.io.IOException;
import java.util.UUID;

import org.jboss.logging.Logger;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CorrelationIdFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private static final Logger LOG = Logger.getLogger(CorrelationIdFilter.class);

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final ThreadLocal<UUID> correlationId = new ThreadLocal<>();

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String correlationIdHeader = requestContext.getHeaderString(CORRELATION_ID_HEADER);
        UUID id;

        if (correlationIdHeader != null && !correlationIdHeader.isEmpty()) {
            try {
                id = UUID.fromString(correlationIdHeader);
                LOG.infof("Using existing correlation ID: %s", id);
            } catch (IllegalArgumentException e) {
                id = UUID.randomUUID();
                LOG.warnf("Invalid correlation ID header, generated new one: %s", id);
            }
        } else {
            id = UUID.randomUUID();
            LOG.infof("No correlation ID in request, generated new one: %s", id);
        }

        correlationId.set(id);
        LOG.infof("[%s] %s %s", id, requestContext.getMethod(), requestContext.getUriInfo().getPath());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        UUID id = correlationId.get();
        if (id != null) {
            responseContext.getHeaders().add(CORRELATION_ID_HEADER, id.toString());
            LOG.infof("[%s] Response status: %d", id, responseContext.getStatus());
        }

        correlationId.remove();
    }

    public static UUID getCurrentCorrelationId() {
        return correlationId.get();
    }
}
