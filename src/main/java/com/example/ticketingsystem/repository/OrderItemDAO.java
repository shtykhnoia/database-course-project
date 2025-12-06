package com.example.ticketingsystem.repository;

import com.example.ticketingsystem.mapper.OrderItemRowMapper;
import com.example.ticketingsystem.model.OrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class OrderItemDAO {

    private final JdbcTemplate jdbcTemplate;

    public OrderItemDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public OrderItem create(OrderItem orderItem) {
        String query = """
                INSERT INTO order_items (order_id, ticket_category_id, quantity, unit_price)
                VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setLong(1, orderItem.getOrderId());
            ps.setLong(2, orderItem.getTicketCategoryId());
            ps.setInt(3, orderItem.getQuantity());
            ps.setBigDecimal(4, orderItem.getUnitPrice());
            return ps;
        }, keyHolder);

        orderItem.setId(keyHolder.getKey().longValue());
        return orderItem;
    }

    public List<OrderItem> findByOrderId(Long orderId) {
        String query = """
                SELECT id, order_id, ticket_category_id, quantity, unit_price
                FROM order_items
                WHERE order_id = ?
                """;
        return jdbcTemplate.query(query, new OrderItemRowMapper(), orderId);
    }
}
