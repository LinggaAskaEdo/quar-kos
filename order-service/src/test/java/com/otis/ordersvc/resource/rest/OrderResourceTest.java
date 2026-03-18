package com.otis.ordersvc.resource.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.otis.ordersvc.dto.CreateOrderRequest;
import com.otis.ordersvc.dto.OrderDTO;
import com.otis.ordersvc.dto.OrderSearchResponse;
import com.otis.ordersvc.dto.ProductDTO;
import com.otis.ordersvc.dto.ProductSearchResponse;
import com.otis.ordersvc.service.OrderService;

import jakarta.ws.rs.core.Response;

@ExtendWith(MockitoExtension.class)
class OrderResourceTest {
	@Mock
	OrderService orderService;

	@InjectMocks
	OrderResource orderResource;

	@Captor
	ArgumentCaptor<Map<String, String>> filtersCaptor;

	private UUID orderId;
	private UUID userId;
	private UUID productId;
	private OrderDTO sampleOrder;
	private ProductDTO sampleProduct;
	private CreateOrderRequest createOrderRequest;

	@BeforeEach
	void setUp() {
		orderId = UUID.randomUUID();
		userId = UUID.randomUUID();
		productId = UUID.randomUUID();

		sampleOrder = new OrderDTO(
				orderId,
				userId,
				"testuser",
				new BigDecimal("100.00"),
				"PENDING",
				null,
				LocalDateTime.now(),
				List.of());

		sampleProduct = new ProductDTO(
				productId,
				"Test Product",
				null,
				new BigDecimal("25.00"),
				10);

		createOrderRequest = new CreateOrderRequest(userId, "testuser", List.of());
	}

	// --- createOrder ---
	@Test
	void createOrder_shouldReturnCreatedWithOrder() {
		when(orderService.createOrder(any(UUID.class), anyString(), anyList())).thenReturn(sampleOrder);

		Response response = orderResource.createOrder(createOrderRequest);

		assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
		assertEquals(sampleOrder, response.getEntity());
		verify(orderService).createOrder(userId, "testuser", List.of());
	}

	// --- getAllOrders ---
	@Test
	void getAllOrders_shouldReturnListOfOrders() {
		List<OrderDTO> orders = List.of(sampleOrder);
		when(orderService.getAllOrders()).thenReturn(orders);

		List<OrderDTO> result = orderResource.getAllOrders();

		assertEquals(orders, result);
		verify(orderService).getAllOrders();
	}

	// --- getOrderById ---
	@Test
	void getOrderById_whenFound_shouldReturnOkWithOrder() {
		when(orderService.getOrderById(orderId)).thenReturn(Optional.of(sampleOrder));

		Response response = orderResource.getOrderById(orderId);

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(sampleOrder, response.getEntity());
	}

	@Test
	void getOrderById_whenNotFound_shouldReturnNotFound() {
		when(orderService.getOrderById(orderId)).thenReturn(Optional.empty());

		Response response = orderResource.getOrderById(orderId);

		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
		assertNull(response.getEntity());
	}

	// --- getOrdersByUserId ---
	@Test
	void getOrdersByUserId_shouldReturnListOfOrders() {
		List<OrderDTO> orders = List.of(sampleOrder);
		when(orderService.getOrdersByUserId(userId)).thenReturn(orders);

		List<OrderDTO> result = orderResource.getOrdersByUserId(userId);

		assertEquals(orders, result);
		verify(orderService).getOrdersByUserId(userId);
	}

	// --- searchOrders ---
	@Test
	void searchOrders_withAllParams_shouldBuildFiltersAndReturnResponse() {
		// given
		String status = "PENDING";
		String userIdStr = userId.toString();
		String username = "testuser";
		String minAmount = "10";
		String maxAmount = "200";
		String sortBy = "created_at";
		String sortDirection = "DESC";
		Integer limit = 50;
		Integer offset = 10;
		List<OrderDTO> orders = List.of(sampleOrder);

		when(orderService.searchOrders(anyMap(), eq(sortBy), eq(sortDirection), eq(limit), eq(offset)))
				.thenReturn(orders);

		// when
		Response response = orderResource.searchOrders(
				status, userIdStr, username, minAmount, maxAmount,
				sortBy, sortDirection, limit, offset);

		// then
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		OrderSearchResponse entity = (OrderSearchResponse) response.getEntity();
		assertEquals(orders.size(), entity.getData().size());
		assertEquals(limit, entity.getLimit());
		assertEquals(offset, entity.getOffset());

		// verify filters map
		verify(orderService).searchOrders(filtersCaptor.capture(), eq(sortBy), eq(sortDirection), eq(limit),
				eq(offset));
		Map<String, String> capturedFilters = filtersCaptor.getValue();
		assertEquals(5, capturedFilters.size());
		assertEquals(status, capturedFilters.get("status"));
		assertEquals(userIdStr, capturedFilters.get("userId"));
		assertEquals(username, capturedFilters.get("username"));
		assertEquals(minAmount, capturedFilters.get("minAmount"));
		assertEquals(maxAmount, capturedFilters.get("maxAmount"));
	}

	@Test
	void searchOrders_withSomeParams_shouldBuildFiltersOnlyForProvidedParams() {
		// given
		String status = "PENDING";
		String userIdStr = userId.toString();
		String sortBy = "created_at";
		String sortDirection = "DESC";
		Integer limit = 100;
		Integer offset = 0;
		List<OrderDTO> orders = List.of(sampleOrder);

		when(orderService.searchOrders(anyMap(), eq(sortBy), eq(sortDirection), eq(limit), eq(offset)))
				.thenReturn(orders);

		// when
		Response response = orderResource.searchOrders(
				status, userIdStr, null, null, null,
				sortBy, sortDirection, limit, offset);

		// then
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

		verify(orderService).searchOrders(filtersCaptor.capture(), eq(sortBy), eq(sortDirection), eq(limit),
				eq(offset));
		Map<String, String> capturedFilters = filtersCaptor.getValue();
		assertEquals(2, capturedFilters.size());
		assertEquals(status, capturedFilters.get("status"));
		assertEquals(userIdStr, capturedFilters.get("userId"));
		assertNull(capturedFilters.get("username"));
		assertNull(capturedFilters.get("minAmount"));
		assertNull(capturedFilters.get("maxAmount"));
	}

	@Test
	void searchOrders_withEmptyStringParams_shouldNotAddToFilters() {
		// given
		String status = "";
		String userIdStr = "";
		String username = "   "; // blank
		String minAmount = null;
		String maxAmount = "";
		String sortBy = "created_at";
		String sortDirection = "DESC";
		Integer limit = 100;
		Integer offset = 0;
		List<OrderDTO> orders = List.of(sampleOrder);

		when(orderService.searchOrders(anyMap(), eq(sortBy), eq(sortDirection), eq(limit), eq(offset)))
				.thenReturn(orders);

		// when
		Response response = orderResource.searchOrders(
				status, userIdStr, username, minAmount, maxAmount,
				sortBy, sortDirection, limit, offset);

		// then
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

		verify(orderService).searchOrders(filtersCaptor.capture(), eq(sortBy), eq(sortDirection), eq(limit),
				eq(offset));
		Map<String, String> capturedFilters = filtersCaptor.getValue();
		assertFalse(capturedFilters.isEmpty());
	}

	@Test
	void searchOrders_withDefaultValues_shouldUseDefaults() {
		// given
		List<OrderDTO> orders = List.of(sampleOrder);
		when(orderService.searchOrders(anyMap(), eq("created_at"), eq("DESC"), eq(100), eq(0))).thenReturn(orders);

		// when – no query params provided (all null, so defaults apply)
		Response response = orderResource.searchOrders(null, null, null, null, null, "created_at", "DESC", 100, 0);

		// then
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		verify(orderService).searchOrders(anyMap(), eq("created_at"), eq("DESC"), eq(100), eq(0));
	}

	// --- getAllProducts ---
	@Test
	void getAllProducts_shouldReturnListOfProducts() {
		List<ProductDTO> products = List.of(sampleProduct);
		when(orderService.getAllProducts()).thenReturn(products);

		List<ProductDTO> result = orderResource.getAllProducts();

		assertEquals(products, result);
		verify(orderService).getAllProducts();
	}

	// --- getProductById ---
	@Test
	void getProductById_whenFound_shouldReturnOkWithProduct() {
		when(orderService.getProductById(productId)).thenReturn(Optional.of(sampleProduct));

		Response response = orderResource.getProductById(productId);

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(sampleProduct, response.getEntity());
	}

	@Test
	void getProductById_whenNotFound_shouldReturnNotFound() {
		when(orderService.getProductById(productId)).thenReturn(Optional.empty());

		Response response = orderResource.getProductById(productId);

		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
		assertNull(response.getEntity());
	}

	// --- searchProducts ---
	@Test
	void searchProducts_withAllParams_shouldBuildFiltersAndReturnResponse() {
		// given
		String name = "test";
		String description = "desc";
		String minPrice = "10.50";
		String maxPrice = "99.99";
		String stock = "5";
		String sortBy = "name";
		String sortDirection = "ASC";
		Integer limit = 25;
		Integer offset = 5;
		List<ProductDTO> products = List.of(sampleProduct);

		when(orderService.searchProducts(anyMap(), eq(sortBy), eq(sortDirection), eq(limit), eq(offset)))
				.thenReturn(products);

		// when
		Response response = orderResource.searchProducts(
				name, description, minPrice, maxPrice, stock,
				sortBy, sortDirection, limit, offset);

		// then
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		ProductSearchResponse entity = (ProductSearchResponse) response.getEntity();
		assertEquals(products.size(), entity.getData().size());
		assertEquals(limit, entity.getLimit());
		assertEquals(offset, entity.getOffset());

		verify(orderService).searchProducts(filtersCaptor.capture(), eq(sortBy), eq(sortDirection), eq(limit),
				eq(offset));
		Map<String, String> capturedFilters = filtersCaptor.getValue();
		assertEquals(5, capturedFilters.size());
		assertEquals(name, capturedFilters.get("name"));
		assertEquals(description, capturedFilters.get("description"));
		assertEquals(minPrice, capturedFilters.get("minPrice"));
		assertEquals(maxPrice, capturedFilters.get("maxPrice"));
		assertEquals(stock, capturedFilters.get("stock"));
	}

	@Test
	void searchProducts_withSomeParams_shouldBuildFiltersOnlyForProvidedParams() {
		// given
		String name = "test";
		String minPrice = "10.50";
		String sortBy = "name";
		String sortDirection = "ASC";
		Integer limit = 25;
		Integer offset = 5;
		List<ProductDTO> products = List.of(sampleProduct);

		when(orderService.searchProducts(anyMap(), eq(sortBy), eq(sortDirection), eq(limit), eq(offset)))
				.thenReturn(products);

		// when
		Response response = orderResource.searchProducts(
				name, null, minPrice, null, null,
				sortBy, sortDirection, limit, offset);

		// then
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

		verify(orderService).searchProducts(filtersCaptor.capture(), eq(sortBy), eq(sortDirection), eq(limit),
				eq(offset));
		Map<String, String> capturedFilters = filtersCaptor.getValue();
		assertEquals(2, capturedFilters.size());
		assertEquals(name, capturedFilters.get("name"));
		assertEquals(minPrice, capturedFilters.get("minPrice"));
		assertNull(capturedFilters.get("description"));
		assertNull(capturedFilters.get("maxPrice"));
		assertNull(capturedFilters.get("stock"));
	}

	@Test
	void searchProducts_withEmptyStringParams_shouldNotAddToFilters() {
		// given
		String name = "";
		String description = "   ";
		String minPrice = null;
		String maxPrice = "";
		String stock = "0";
		String sortBy = "id";
		String sortDirection = "DESC";
		Integer limit = 100;
		Integer offset = 0;
		List<ProductDTO> products = List.of(sampleProduct);

		when(orderService.searchProducts(anyMap(), eq(sortBy), eq(sortDirection), eq(limit), eq(offset)))
				.thenReturn(products);

		// when
		Response response = orderResource.searchProducts(
				name, description, minPrice, maxPrice, stock,
				sortBy, sortDirection, limit, offset);

		// then
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

		verify(orderService).searchProducts(filtersCaptor.capture(), eq(sortBy), eq(sortDirection), eq(limit),
				eq(offset));
		Map<String, String> capturedFilters = filtersCaptor.getValue();
		// stock is "0" (non-empty) so it should be included
		assertEquals(2, capturedFilters.size());
		assertEquals(stock, capturedFilters.get("stock"));
	}

	@Test
	void searchProducts_withDefaultValues_shouldUseDefaults() {
		// given
		List<ProductDTO> products = List.of(sampleProduct);
		when(orderService.searchProducts(anyMap(), eq("id"), eq("DESC"), eq(100), eq(0)))
				.thenReturn(products);

		// when – no query params (all null, so defaults apply)
		Response response = orderResource.searchProducts(null, null, null, null, null, "id", "DESC", 100, 0);

		// then
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		verify(orderService).searchProducts(anyMap(), eq("id"), eq("DESC"), eq(100), eq(0));
	}
}