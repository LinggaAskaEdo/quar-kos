package com.otis.usersvc.kafka;

import java.util.UUID;

import org.apache.kafka.common.KafkaException;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otis.usersvc.dto.UserEvent;

import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserEventProducer {
    private static final Logger LOG = Logger.getLogger(UserEventProducer.class);

    @Channel("user-events")
    Emitter<Record<String, String>> emitter;

    ObjectMapper objectMapper;

    public UserEventProducer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void sendUserCreatedEvent(UUID correlationId, UUID userId, String username, String email) {
        UserEvent event = UserEvent.createdUser(correlationId, userId, username, email);

        try {
            emitter.send(Record.of(userId.toString(), objectMapper.writeValueAsString(event)));
            LOG.infof("Sent USER_CREATED event for user: %s", username);
        } catch (Exception e) {
            throw new KafkaException("Failed to serialize event", e);
        }
    }

    public void sendUserProfileCreatedEvent(UUID correlationId, UUID userId, String firstName, String lastName) {
        UserEvent event = UserEvent.createdUserProfile(correlationId, userId, firstName, lastName);

        try {
            emitter.send(Record.of(userId.toString(), objectMapper.writeValueAsString(event)));
            LOG.infof("Sent USER_PROFILE_CREATED event for userId: %d", userId);
        } catch (Exception e) {
            throw new KafkaException("Failed to serialize event", e);
        }
    }
}
