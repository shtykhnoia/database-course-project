package com.example.ticketingsystem.mapper;

import com.example.ticketingsystem.model.Order;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderRowMapper implements RowMapper<Order> {
    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setOrderNumber(rs.getString("order_number"));
        order.setUserId(rs.getLong("user_id"));
        order.setStatus(rs.getString("status"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setCreatedAt(rs.getObject("created_at", java.time.LocalDateTime.class));
        return order;
    }
}
