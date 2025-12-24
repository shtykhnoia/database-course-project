package com.example.ticketingsystem.repository;

import com.example.ticketingsystem.mapper.OrderRowMapper;
import com.example.ticketingsystem.model.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class OrderDAO {

    private final JdbcTemplate jdbcTemplate;

    public OrderDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Order create(Order order) {
        String query = """
                INSERT INTO orders (order_number, user_id, status, total_amount, created_at)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, order.getOrderNumber());
            ps.setLong(2, order.getUserId());
            ps.setString(3, order.getStatus());
            ps.setBigDecimal(4, order.getTotalAmount());
            ps.setObject(5, order.getCreatedAt());
            return ps;
        }, keyHolder);

        order.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return order;
    }

    public Optional<Order> findById(Long id) {
        String query = """
                SELECT id, order_number, user_id, status, total_amount, created_at
                FROM orders
                WHERE id = ?
                """;
        List<Order> results = jdbcTemplate.query(query, new OrderRowMapper(), id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    public List<Order> findAll() {
        String query = """
                SELECT id, order_number, user_id, status, total_amount, created_at
                FROM orders
                ORDER BY created_at DESC
                """;
        return jdbcTemplate.query(query, new OrderRowMapper());
    }

    public List<Order> findAll(int page, int size) {
        String query = """
                SELECT id, order_number, user_id, status, total_amount, created_at
                FROM orders
                ORDER BY created_at DESC
                LIMIT ? OFFSET ?
                """;
        int offset = page * size;
        return jdbcTemplate.query(query, new OrderRowMapper(), size, offset);
    }

    public List<Order> findByUserId(Long userId) {
        String query = """
                SELECT id, order_number, user_id, status, total_amount, created_at
                FROM orders
                WHERE user_id = ?
                ORDER BY created_at DESC
                """;
        return jdbcTemplate.query(query, new OrderRowMapper(), userId);
    }

    public List<Order> findByStatus(String status) {
        String query = """
                SELECT id, order_number, user_id, status, total_amount, created_at
                FROM orders
                WHERE status = ?
                ORDER BY created_at DESC
                """;
        return jdbcTemplate.query(query, new OrderRowMapper(), status);
    }

    public List<Order> findByStatus(String status, int page, int size) {
        String query = """
                SELECT id, order_number, user_id, status, total_amount, created_at
                FROM orders
                WHERE status = ?
                ORDER BY created_at DESC
                LIMIT ? OFFSET ?
                """;
        int offset = page * size;
        return jdbcTemplate.query(query, new OrderRowMapper(), status, size, offset);
    }

    public Order updateStatus(Long id, String status) {
        String query = """
                UPDATE orders
                SET status = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(query, status, id);
        return findById(id).orElseThrow();
    }

    public Order updateTotalAmount(Long orderId, BigDecimal newPrice) {
        String query = """
                UPDATE orders
                SET total_amount = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(query, newPrice, orderId);
        return findById(orderId).orElseThrow();
    }

    public List<Order> findByEventIdAndStatus(Long eventId, String status) {
        String query = """
                SELECT DISTINCT o.id, o.order_number, o.user_id, o.status, o.total_amount, o.created_at
                FROM orders o
                JOIN order_items oi ON o.id = oi.order_id
                JOIN ticket_categories tc ON oi.ticket_category_id = tc.id
                WHERE tc.event_id = ? AND o.status = ?
                ORDER BY o.created_at DESC
                """;
        return jdbcTemplate.query(query, new OrderRowMapper(), eventId, status);
    }
}
