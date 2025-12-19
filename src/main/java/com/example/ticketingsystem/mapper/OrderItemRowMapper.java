package com.example.ticketingsystem.mapper;

import com.example.ticketingsystem.model.OrderItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItemRowMapper implements RowMapper<OrderItem> {
    @Override
    public OrderItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(rs.getLong("id"));
        orderItem.setOrderId(rs.getLong("order_id"));
        orderItem.setTicketCategoryId(rs.getLong("ticket_category_id"));
        orderItem.setQuantity(rs.getInt("quantity"));
        orderItem.setUnitPrice(rs.getBigDecimal("unit_price"));
        orderItem.setPromoCodeId(rs.getObject("promo_code_id", Long.class));
        return orderItem;
    }
}
