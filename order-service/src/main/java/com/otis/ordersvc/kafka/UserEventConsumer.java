package com.otis.ordersvc.kafka;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserEventConsumer {
    private static final Logger LOG = Logger.getLogger(UserEventConsumer.class);

    @Incoming("user-events")
    public void consumeUserEvent(String message) {
        String correlationId = extractCorrelationId(message);

        LOG.infof("[%s] Received user event: %s", correlationId, message);

        if (message.contains("USER_CREATED")) {
            LOG.infof("[%s] Processing USER_CREATED event", correlationId);
            // TODO Could create initial order data, send welcome offer, etc.

        } else if (message.contains("USER_PROFILE_CREATED")) {
            LOG.infof("[%s] Processing USER_PROFILE_CREATED event", correlationId);
            // TODO Could personalize product recommendations, etc.
        }
    }

    private String extractCorrelationId(String message) {
        try {
            int startIdx = message.indexOf("\"correlationId\":\"");
            if (startIdx != -1) {
                startIdx += 17; // length of "correlationId":"
                int endIdx = message.indexOf("\"", startIdx);
                if (endIdx != -1) {
                    return message.substring(startIdx, endIdx);
                }
            }
        } catch (Exception e) {
            LOG.warn("Failed to extract correlation ID from message", e);
        }

        return "N/A";
    }
}
