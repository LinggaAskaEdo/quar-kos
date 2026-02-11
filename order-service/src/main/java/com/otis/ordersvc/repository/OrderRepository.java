package com.otis.ordersvc.repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import org.jboss.logging.Logger;

import com.otis.common.exception.CreationFailedException;
import com.otis.common.exception.DataAccessException;
import com.otis.common.preference.DatabaseColumns;
import com.otis.common.preference.FilterKey;
import com.otis.common.util.DynamicQueryBuilder;
import com.otis.common.util.SqlQueryLoader;
import com.otis.ordersvc.dto.OrderDTO;
import com.otis.ordersvc.dto.OrderItemDTO;
import com.otis.ordersvc.dto.ProductDTO;
import com.otis.ordersvc.util.DtoMapper;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderRepository {
	private static final Logger LOG = Logger.getLogger(OrderRepository.class);
	private static final String QUERY_FILE = "order-queries.sql";

	private final DataSource dataSource;

	public OrderRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public List<OrderDTO> findAll() {
		String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "findAllOrders");
		List<OrderDTO> orders = new ArrayList<>();

		try (Connection conn = dataSource.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				orders.add(DtoMapper.mapToOrderDTO(rs));
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error finding all orders", e);
		}

		return orders;
	}

	public Optional<OrderDTO> findById(UUID id) {
		String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "findOrderWithItems");

		try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setObject(1, id);

			try (ResultSet rs = stmt.executeQuery()) {
				return DtoMapper.buildOrderWithItemsDTO(rs);
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error finding order by id", e);
		}
	}

	public List<OrderDTO> findByUserId(UUID userId) {
		String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "findOrdersByUserId");
		List<OrderDTO> orders = new ArrayList<>();

		try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setObject(1, userId);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					orders.add(DtoMapper.mapToOrderDTO(rs));
				}
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error finding orders by user id", e);
		}

		return orders;
	}

	public OrderDTO create(UUID userId, String username, BigDecimal totalAmount, String status, UUID correlationId) {
		String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "insertOrder");

		try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setObject(1, userId);
			stmt.setString(2, username);
			stmt.setBigDecimal(3, totalAmount);
			stmt.setString(4, status);
			stmt.setObject(5, correlationId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return DtoMapper.mapToOrderDTO(rs);
				}
			}
		} catch (SQLException e) {
			throw new CreationFailedException("Error creating order", e);
		}

		throw new CreationFailedException("Failed to create order");
	}

	public OrderItemDTO createOrderItem(UUID orderId, UUID productId, Integer quantity,
			String productName, String productDescription, BigDecimal price) {
		String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "insertOrderItem");

		try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setObject(1, orderId);
			stmt.setObject(2, productId);
			stmt.setInt(3, quantity);
			stmt.setBigDecimal(4, price);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return DtoMapper.mapToOrderItemDTO(rs, productName, productDescription);
				}
			}
		} catch (SQLException e) {
			throw new CreationFailedException("Error creating order item", e);
		}

		throw new CreationFailedException("Failed to create order item");
	}

	public List<ProductDTO> findAllProducts() {
		String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "findAllProducts");
		List<ProductDTO> products = new ArrayList<>();

		try (Connection conn = dataSource.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				products.add(DtoMapper.mapToProductDTO(rs));
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error finding all products", e);
		}

		return products;
	}

	public Optional<ProductDTO> findProductById(UUID id) {
		String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "findProductById");

		try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setObject(1, id);

			LOG.debugf("Executing query: %s", stmt.toString());

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return Optional.of(DtoMapper.mapToProductDTO(rs));
				}
			}
		} catch (SQLException e) {
			LOG.error("Error when findProductById: ", e);
			throw new DataAccessException("Error finding product by id", e);
		}

		return Optional.empty();
	}

	public List<OrderDTO> findByFilters(Map<String, String> filters, String sortBy, String sortDirection,
			Integer limit, Integer offset) {
		DynamicQueryBuilder queryBuilder = new DynamicQueryBuilder("orders");
		queryBuilder.select(DatabaseColumns.ID, DatabaseColumns.USER_ID, DatabaseColumns.USERNAME,
				DatabaseColumns.TOTAL_AMOUNT, DatabaseColumns.STATUS, DatabaseColumns.CORRELATION_ID,
				DatabaseColumns.CREATED_AT);

		// Apply filters
		if (filters.containsKey(FilterKey.STATUS)) {
			queryBuilder.where(DatabaseColumns.STATUS, "=", filters.get("status"));
		}

		if (filters.containsKey(FilterKey.USER_ID)) {
			queryBuilder.where(DatabaseColumns.USER_ID, "=", UUID.fromString(filters.get("userId")));
		}

		if (filters.containsKey(FilterKey.USERNAME)) {
			queryBuilder.where(DatabaseColumns.USERNAME, "ILIKE", "%" + filters.get("username") + "%");
		}

		if (filters.containsKey(FilterKey.MIN_AMOUNT)) {
			queryBuilder.where(DatabaseColumns.TOTAL_AMOUNT, ">=", new BigDecimal(filters.get("minAmount")));
		}

		if (filters.containsKey(FilterKey.MAX_AMOUNT)) {
			queryBuilder.where(DatabaseColumns.TOTAL_AMOUNT, "<=", new BigDecimal(filters.get("maxAmount")));
		}

		// Apply sorting
		if (sortBy != null && !sortBy.isEmpty()) {
			String direction = (sortDirection != null && sortDirection.equalsIgnoreCase("ASC")) ? "ASC" : "DESC";
			queryBuilder.orderBy(sortBy, direction);
		} else {
			queryBuilder.orderBy(DatabaseColumns.CREATED_AT, "DESC");
		}

		// Apply pagination
		if (limit != null) {
			queryBuilder.limit(limit);
		}

		if (offset != null) {
			queryBuilder.offset(offset);
		}

		String sql = queryBuilder.buildQuery();
		List<OrderDTO> orders = new ArrayList<>();

		try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			queryBuilder.setParameters(stmt);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					orders.add(DtoMapper.mapToOrderDTO(rs));
				}
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error executing dynamic query", e);
		}

		return orders;
	}
}
