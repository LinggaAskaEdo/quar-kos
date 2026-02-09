package com.otis.usersvc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;

@Singleton
public class JacksonConfig implements ObjectMapperCustomizer {
	@Override
	public void customize(ObjectMapper objectMapper) {
		// Register Java 8 time module
		objectMapper.registerModule(new JavaTimeModule());

		// Disable FAIL_ON_EMPTY_BEANS for native mode
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

		// Write dates as timestamps
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}
}
