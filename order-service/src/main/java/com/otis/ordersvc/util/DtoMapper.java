package com.otis.ordersvc.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.otis.ordersvc.model.Order;
import com.otis.ordersvc.model.OrderItem;
import com.otis.ordersvc.model.Product;

public class DtoMapper {
    private DtoMapper() {
    }

    public static Order mapToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId((UUID) rs.getObject("id"));
        order.setUserId((UUID) rs.getObject("user_id"));
        order.setUsername(rs.getString("username"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setStatus(rs.getString("status"));
        Object correlationIdObj = rs.getObject("correlation_id");
        if (correlationIdObj != null) {
            order.setCorrelationId((UUID) correlationIdObj);
        }
        order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return order;
    }

    public static Product mapToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId((UUID) rs.getObject("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setStock(rs.getInt("stock"));
        return product;
    }

    public static Optional<Order> buildOrderWithItems(ResultSet rs) throws SQLException {
        if (!rs.next()) {
            return Optional.empty();
        }

        Order order = new Order();
        order.setId((UUID) rs.getObject("order_id"));
        order.setUserId((UUID) rs.getObject("user_id"));
        order.setUsername(rs.getString("username"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setStatus(rs.getString("status"));

        Object correlationIdObj = rs.getObject("correlation_id");
        if (correlationIdObj != null) {
            order.setCorrelationId((UUID) correlationIdObj);
        }

        order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        List<OrderItem> items = new ArrayList<>();

        do {
            Object itemIdObj = rs.getObject("item_id");
            if (itemIdObj != null) {
                OrderItem item = new OrderItem();
                item.setId((UUID) itemIdObj);
                item.setOrderId(order.getId());
                item.setProductId((UUID) rs.getObject("product_id"));
                item.setProductName(rs.getString("product_name"));
                item.setProductDescription(rs.getString("product_description"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getBigDecimal("item_price"));

                items.add(item);
            }
        } while (rs.next());

        order.setItems(items);

        return Optional.of(order);
    }
}
