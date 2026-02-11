package com.otis.ordersvc.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.otis.ordersvc.dto.CreateOrderRequest;
import com.otis.ordersvc.dto.OrderDTO;
import com.otis.ordersvc.dto.ProductDTO;
import com.otis.ordersvc.dto.SearchResponse;
import com.otis.ordersvc.service.OrderService;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {
	private final OrderService orderService;

	public OrderResource(OrderService orderService) {
		this.orderService = orderService;
	}

	@POST
	@Path("/orders")
	public Response createOrder(CreateOrderRequest request) {
		OrderDTO created = orderService.createOrder(request.getUserId(), request.getUsername(), request.getItems());

		return Response.status(Response.Status.CREATED).entity(created).build();
	}

	@GET
	@Path("/orders")
	public List<OrderDTO> getAllOrders() {
		return orderService.getAllOrders();
	}

	@GET
	@Path("/orders/{id}")
	public Response getOrderById(@PathParam("id") UUID id) {
		return orderService.getOrderById(id)
				.map(order -> Response.ok(order).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@GET
	@Path("/orders/user/{userId}")
	public List<OrderDTO> getOrdersByUserId(@PathParam("userId") UUID userId) {
		return orderService.getOrdersByUserId(userId);
	}

	@GET
	@Path("/orders/search")
	public Response searchOrders(
			@QueryParam("status") String status,
			@QueryParam("userId") String userId,
			@QueryParam("username") String username,
			@QueryParam("minAmount") String minAmount,
			@QueryParam("maxAmount") String maxAmount,
			@QueryParam("sortBy") @DefaultValue("created_at") String sortBy,
			@QueryParam("sortDirection") @DefaultValue("DESC") String sortDirection,
			@QueryParam("limit") @DefaultValue("100") Integer limit,
			@QueryParam("offset") @DefaultValue("0") Integer offset) {

		Map<String, String> filters = new HashMap<>();
		if (status != null && !status.isEmpty()) {
			filters.put("status", status);
		}
		if (userId != null && !userId.isEmpty()) {
			filters.put("userId", userId);
		}
		if (username != null && !username.isEmpty()) {
			filters.put("username", username);
		}
		if (minAmount != null && !minAmount.isEmpty()) {
			filters.put("minAmount", minAmount);
		}
		if (maxAmount != null && !maxAmount.isEmpty()) {
			filters.put("maxAmount", maxAmount);
		}

		List<OrderDTO> orders = orderService.searchOrders(filters, sortBy, sortDirection, limit, offset);

		return Response.ok(new SearchResponse(orders, filters, limit, offset)).build();
	}

	@GET
	@Path("/products")
	public List<ProductDTO> getAllProducts() {
		return orderService.getAllProducts();
	}

	@GET
	@Path("/products/{id}")
	public Response getProductById(@PathParam("id") UUID id) {
		return orderService.getProductById(id)
				.map(product -> Response.ok(product).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}
}
