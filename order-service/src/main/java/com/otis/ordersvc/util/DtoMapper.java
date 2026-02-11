package com.otis.ordersvc.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.otis.common.preference.DatabaseColumns;
import com.otis.ordersvc.dto.OrderDTO;
import com.otis.ordersvc.dto.OrderItemDTO;
import com.otis.ordersvc.dto.ProductDTO;

public class DtoMapper {
	private DtoMapper() {
	}

	public static OrderDTO mapToOrderDTO(ResultSet rs) throws SQLException {
		return new OrderDTO(
				extractUUID(rs, DatabaseColumns.ID),
				extractUUID(rs, DatabaseColumns.USER_ID),
				rs.getString(DatabaseColumns.USERNAME),
				rs.getBigDecimal(DatabaseColumns.TOTAL_AMOUNT),
				rs.getString(DatabaseColumns.STATUS),
				extractUUID(rs, DatabaseColumns.CORRELATION_ID),
				extractLocalDateTime(rs, DatabaseColumns.CREATED_AT),
				List.of() // no items; use buildOrderWithItemsDTO() for items
		);
	}

	public static ProductDTO mapToProductDTO(ResultSet rs) throws SQLException {
		return new ProductDTO(
				extractUUID(rs, DatabaseColumns.ID),
				rs.getString(DatabaseColumns.NAME),
				rs.getString(DatabaseColumns.DESCRIPTION),
				rs.getBigDecimal(DatabaseColumns.PRICE),
				rs.getInt(DatabaseColumns.STOCK));
	}

	public static OrderItemDTO mapToOrderItemDTO(
			ResultSet rs,
			String productName,
			String productDescription) throws SQLException {
		return new OrderItemDTO(
				extractUUID(rs, DatabaseColumns.ID),
				extractUUID(rs, DatabaseColumns.ORDER_ID),
				extractUUID(rs, DatabaseColumns.PRODUCT_ID),
				productName,
				productDescription,
				rs.getInt(DatabaseColumns.QUANTITY),
				rs.getBigDecimal(DatabaseColumns.PRICE));
	}

	// ---------- Order with its items (from JOIN query) ----------
	public static Optional<OrderDTO> buildOrderWithItemsDTO(ResultSet rs) throws SQLException {
		if (!rs.next()) {
			return Optional.empty();
		}

		// Build the order DTO from the first row (no items yet)
		OrderDTO order = buildOrderDTOFromCurrentRow(rs);
		List<OrderItemDTO> items = new ArrayList<>();

		// Process first row item if present
		processOrderItemRowDTO(rs, order, items);

		// Process remaining rows
		while (rs.next()) {
			processOrderItemRowDTO(rs, order, items);
		}

		// Return a new OrderDTO with the collected items
		return Optional.of(new OrderDTO(
				order.id(),
				order.userId(),
				order.username(),
				order.totalAmount(),
				order.status(),
				order.correlationId(),
				order.createdAt(),
				items));
	}

	// ---------- Helper methods ----------
	private static OrderDTO buildOrderDTOFromCurrentRow(ResultSet rs) throws SQLException {
		return new OrderDTO(
				extractUUID(rs, DatabaseColumns.ORDER_ID),
				extractUUID(rs, DatabaseColumns.USER_ID),
				rs.getString(DatabaseColumns.USERNAME),
				rs.getBigDecimal(DatabaseColumns.TOTAL_AMOUNT),
				rs.getString(DatabaseColumns.STATUS),
				extractUUID(rs, DatabaseColumns.CORRELATION_ID),
				extractLocalDateTime(rs, DatabaseColumns.CREATED_AT),
				List.of() // placeholder, will be replaced later
		);
	}

	private static void processOrderItemRowDTO(ResultSet rs, OrderDTO order, List<OrderItemDTO> items)
			throws SQLException {
		UUID itemId = extractUUID(rs, DatabaseColumns.ITEM_ID);
		if (itemId != null) {
			OrderItemDTO item = new OrderItemDTO(
					itemId,
					order.id(), // orderId
					extractUUID(rs, DatabaseColumns.PRODUCT_ID),
					rs.getString(DatabaseColumns.PRODUCT_NAME),
					rs.getString(DatabaseColumns.PRODUCT_DESCRIPTION),
					rs.getInt(DatabaseColumns.QUANTITY),
					rs.getBigDecimal(DatabaseColumns.ITEM_PRICE));
			items.add(item);
		}
	}

	// ---------- Utility methods for ResultSet ----------
	private static UUID extractUUID(ResultSet rs, String columnName) throws SQLException {
		Object obj = rs.getObject(columnName);
		return (obj != null && !rs.wasNull()) ? (UUID) obj : null;
	}

	private static LocalDateTime extractLocalDateTime(ResultSet rs, String columnName) throws SQLException {
		var timestamp = rs.getTimestamp(columnName);
		return timestamp != null ? timestamp.toLocalDateTime() : null;
	}
}
