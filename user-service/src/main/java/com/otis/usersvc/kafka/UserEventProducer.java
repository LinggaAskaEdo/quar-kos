package com.otis.usersvc.kafka;

import java.util.UUID;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserEventProducer {
    private static final Logger LOG = Logger.getLogger(UserEventProducer.class);

    @Channel("user-events")
    Emitter<Record<String, String>> emitter;

    public void sendUserCreatedEvent(UUID userId, String username, String email) {
        String message = String.format(
                "{\"event\":\"USER_CREATED\",\"userId\":%s,\"username\":\"%s\",\"email\":\"%s\"}",
                userId, username, email);

        emitter.send(Record.of(userId.toString(), message));
        LOG.infof("Sent USER_CREATED event for user: %s", username);
    }

    public void sendUserProfileCreatedEvent(UUID userId, String firstName, String lastName) {
        String message = String.format(
                "{\"event\":\"USER_PROFILE_CREATED\",\"userId\":%s,\"firstName\":\"%s\",\"lastName\":\"%s\"}",
                userId, firstName, lastName);

        emitter.send(Record.of(userId.toString(), message));
        LOG.infof("Sent USER_PROFILE_CREATED event for userId: %d", userId);
    }
}
