package com.otis.ordersvc.resource.kafka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otis.common.dto.UserEvent;
import com.otis.common.util.CorrelationIdFilter;
import com.otis.ordersvc.dto.OrderItemRequest;
import com.otis.ordersvc.dto.ProductDTO;
import com.otis.ordersvc.service.OrderService;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserEventConsumer {
	private static final Logger LOG = Logger.getLogger(UserEventConsumer.class);

	private final ObjectMapper objectMapper;
	private final OrderService orderService;

	public UserEventConsumer(ObjectMapper objectMapper, OrderService orderService) {
		this.objectMapper = objectMapper;
		this.orderService = orderService;
	}

	@Incoming("user-events")
	public void consumeUserEvent(String message) {
		try {
			String correlationId = extractCorrelationId(message);
			CorrelationIdFilter.setCurrentCorrelationId(UUID.fromString(correlationId));

			LOG.infof("[%s] Received user event: %s", correlationId, message);
			UserEvent event = objectMapper.readValue(message, UserEvent.class);

			if (event.event().equalsIgnoreCase("USER_CREATED")) {
				LOG.infof("[%s] Processing USER_CREATED event", correlationId);

				ProductDTO products = getProductByName("Book Note");
				if (products == null) {
					LOG.warnf("[%s] Product 'Book Note' not found", correlationId);
					return;
				}

				List<OrderItemRequest> items = new ArrayList<>();
				items.add(new OrderItemRequest(products.id(), 1));

				orderService.createOrder(event.userId(), event.username(), items);
			} else if (event.event().equalsIgnoreCase("USER_PROFILE_CREATED")) {
				LOG.infof("[%s] Processing USER_PROFILE_CREATED event", correlationId);

				ProductDTO products = getProductByName("Key Chain");
				if (products == null) {
					LOG.warnf("[%s] Product 'Key Chain' not found", correlationId);
					return;
				}

				List<OrderItemRequest> items = new ArrayList<>();
				items.add(new OrderItemRequest(products.id(), 1));

				orderService.createOrder(event.userId(), event.username(), items);
			}
		} catch (Exception e) {
			LOG.error("Failed to process user event", e);
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

	private ProductDTO getProductByName(String name) {
		Map<String, String> filters = new HashMap<>();
		filters.put("name", name);
		
		List<ProductDTO> products = orderService.searchProducts(filters, null, null, 1, 0);
		if (!products.isEmpty()) {
			return products.get(0);
		}

		return null;
	}
}
