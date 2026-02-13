package com.otis.usersvc.resource.kafka;

import java.util.UUID;

import org.apache.kafka.common.KafkaException;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otis.common.dto.UserEvent;

import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserEventProducer {
	private static final Logger LOG = Logger.getLogger(UserEventProducer.class);

	@Channel("user-events")
	Emitter<Record<String, String>> emitter;

	private final ObjectMapper objectMapper;

	public UserEventProducer(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public void sendUserCreatedEvent(UUID correlationId, UUID userId, String username) {
		UserEvent event = UserEvent.createdUser(correlationId, userId, username);

		try {
			emitter.send(Record.of(userId.toString(), objectMapper.writeValueAsString(event)));
			LOG.infof("[%s] Sent USER_CREATED event for user: %s", correlationId, username);
		} catch (Exception e) {
			throw new KafkaException("Failed to serialize event", e);
		}
	}

	public void sendUserProfileCreatedEvent(UUID correlationId, UUID userId, String username) {
		UserEvent event = UserEvent.createdUserProfile(correlationId, userId, username);

		try {
			emitter.send(Record.of(userId.toString(), objectMapper.writeValueAsString(event)));
			LOG.infof("Sent USER_PROFILE_CREATED event for userId: %s", username);
		} catch (Exception e) {
			throw new KafkaException("Failed to serialize event", e);
		}
	}
}
