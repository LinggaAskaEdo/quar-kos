package com.otis.ordersvc.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.otis.common.preference.DatabaseColumns;
import com.otis.ordersvc.model.Order;
import com.otis.ordersvc.model.OrderItem;
import com.otis.ordersvc.model.Product;

public class ModelMapper {
	private ModelMapper() {
	}

	public static Order mapToOrder(ResultSet rs) throws SQLException {
		Order order = new Order();
		order.setId((UUID) rs.getObject(DatabaseColumns.ID));
		order.setUserId((UUID) rs.getObject(DatabaseColumns.USER_ID));
		order.setUsername(rs.getString(DatabaseColumns.USERNAME));
		order.setTotalAmount(rs.getBigDecimal(DatabaseColumns.TOTAL_AMOUNT));
		order.setStatus(rs.getString(DatabaseColumns.STATUS));
		Object correlationIdObj = rs.getObject(DatabaseColumns.CORRELATION_ID);
		if (correlationIdObj != null) {
			order.setCorrelationId((UUID) correlationIdObj);
		}
		order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

		return order;
	}

	public static Product mapToProduct(ResultSet rs) throws SQLException {
		Product product = new Product();
		product.setId((UUID) rs.getObject(DatabaseColumns.ID));
		product.setName(rs.getString(DatabaseColumns.NAME));
		product.setDescription(rs.getString(DatabaseColumns.DESCRIPTION));
		product.setPrice(rs.getBigDecimal(DatabaseColumns.PRICE));
		product.setStock(rs.getInt(DatabaseColumns.STOCK));

		return product;
	}

	public static Optional<Order> buildOrderWithItems(ResultSet rs) throws SQLException {
		if (!rs.next()) {
			return Optional.empty();
		}

		Order order = new Order();
		order.setId((UUID) rs.getObject(DatabaseColumns.ORDER_ID));
		order.setUserId((UUID) rs.getObject(DatabaseColumns.USER_ID));
		order.setUsername(rs.getString(DatabaseColumns.USERNAME));
		order.setTotalAmount(rs.getBigDecimal(DatabaseColumns.TOTAL_AMOUNT));
		order.setStatus(rs.getString(DatabaseColumns.STATUS));

		Object correlationIdObj = rs.getObject(DatabaseColumns.CORRELATION_ID);
		if (correlationIdObj != null) {
			order.setCorrelationId((UUID) correlationIdObj);
		}

		order.setCreatedAt(rs.getTimestamp(DatabaseColumns.CREATED_AT).toLocalDateTime());

		List<OrderItem> items = new ArrayList<>();

		do {
			Object itemIdObj = rs.getObject(DatabaseColumns.ITEM_ID);
			if (itemIdObj != null) {
				OrderItem item = new OrderItem();
				item.setId((UUID) itemIdObj);
				item.setOrderId(order.getId());
				item.setProductId((UUID) rs.getObject(DatabaseColumns.PRODUCT_ID));
				item.setProductName(rs.getString(DatabaseColumns.PRODUCT_NAME));
				item.setProductDescription(rs.getString(DatabaseColumns.PRODUCT_DESCRIPTION));
				item.setQuantity(rs.getInt(DatabaseColumns.QUANTITY));
				item.setPrice(rs.getBigDecimal(DatabaseColumns.ITEM_PRICE));

				items.add(item);
			}
		} while (rs.next());

		order.setItems(items);

		return Optional.of(order);
	}
}
