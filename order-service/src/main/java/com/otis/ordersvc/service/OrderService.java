package com.otis.ordersvc.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jboss.logging.Logger;

import com.otis.ordersvc.dto.OrderItemRequest;
import com.otis.ordersvc.model.Order;
import com.otis.ordersvc.model.Product;
import com.otis.ordersvc.repository.OrderRepository;
import com.otis.ordersvc.util.CorrelationIdFilter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class OrderService {
    private static final Logger LOG = Logger.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders() {
        UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
        LOG.infof("[%s] Getting all orders", correlationId);

        return orderRepository.findAll();
    }

    @Transactional
    public Order createOrder(UUID userId, String username, List<OrderItemRequest> itemRequests) {
        UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
        LOG.infof("[%s] Creating order for user: %s", correlationId, username);

        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequest itemReq : itemRequests) {
            Optional<Product> product = orderRepository.findProductById(itemReq.getProductId());
            if (product.isPresent()) {
                BigDecimal itemTotal = product.get().getPrice()
                        .multiply(new BigDecimal(itemReq.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);
            }
        }

        // Create order with correlation ID
        Order order = orderRepository.create(userId, username, totalAmount, "PENDING", correlationId);

        // Create order items
        for (OrderItemRequest itemReq : itemRequests) {
            Optional<Product> product = orderRepository.findProductById(itemReq.getProductId());
            if (product.isPresent()) {
                orderRepository.createOrderItem(
                        order.getId(),
                        itemReq.getProductId(),
                        itemReq.getQuantity(),
                        product.get().getPrice());
            }
        }

        LOG.infof("[%s] Order created successfully: %s", correlationId, order.getId());
        return orderRepository.findById(order.getId()).orElseThrow();
    }
}
