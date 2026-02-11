package com.otis.ordersvc.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.jboss.logging.Logger;

import com.otis.common.util.CorrelationIdFilter;
import com.otis.ordersvc.dto.OrderDTO;
import com.otis.ordersvc.dto.OrderItemRequest;
import com.otis.ordersvc.dto.ProductDTO;
import com.otis.ordersvc.repository.OrderRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class OrderService {
	private static final Logger LOG = Logger.getLogger(OrderService.class);

	private final OrderRepository orderRepository;

	public OrderService(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	public List<OrderDTO> getAllOrders() {
		UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
		LOG.infof("[%s] Getting all orders", correlationId);

		return orderRepository.findAll();
	}

	public Optional<OrderDTO> getOrderById(UUID id) {
		UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
		LOG.infof("[%s] Getting order by id: %s", correlationId, id);

		return orderRepository.findById(id);
	}

	public List<OrderDTO> getOrdersByUserId(UUID userId) {
		UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
		LOG.infof("[%s] Getting orders for user: %s", correlationId, userId);
		return orderRepository.findByUserId(userId);
	}

	@Transactional
	public OrderDTO createOrder(UUID userId, String username, List<OrderItemRequest> itemRequests) {
		UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
		LOG.infof("[%s] Creating order for user: %s", correlationId, username);

		// Calculate total amount
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (OrderItemRequest itemReq : itemRequests) {
			Optional<ProductDTO> product = orderRepository.findProductById(itemReq.getProductId());
			if (product.isPresent()) {
				BigDecimal itemTotal = product.get().price().multiply(new BigDecimal(itemReq.getQuantity()));
				totalAmount = totalAmount.add(itemTotal);
			}
		}

		// Create order with correlation ID
		OrderDTO order = orderRepository.create(userId, username, totalAmount, "PENDING", correlationId);

		// Create order items
		for (OrderItemRequest itemReq : itemRequests) {
			Optional<ProductDTO> product = orderRepository.findProductById(itemReq.getProductId());
			if (product.isPresent()) {
				orderRepository.createOrderItem(
						order.id(),
						itemReq.getProductId(),
						itemReq.getQuantity(),
						product.get().name(),
						product.get().description(),
						product.get().price());
			}
		}

		LOG.infof("[%s] Order created successfully: %s", correlationId, order.id());
		return orderRepository.findById(order.id()).orElseThrow();
	}

	public List<ProductDTO> getAllProducts() {
		UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
		LOG.infof("[%s] Getting all products", correlationId);

		return orderRepository.findAllProducts();
	}

	public Optional<ProductDTO> getProductById(UUID id) {
		UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
		LOG.infof("[%s] Getting product by id: %s", correlationId, id);

		return orderRepository.findProductById(id);
	}

	public List<OrderDTO> searchOrders(Map<String, String> filters, String sortBy, String sortDirection,
			Integer limit, Integer offset) {
		UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
		LOG.infof("[%s] Searching orders with filters: %s", correlationId, filters);

		return orderRepository.findByFilters(filters, sortBy, sortDirection, limit, offset);
	}
}
