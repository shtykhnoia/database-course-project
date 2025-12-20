package com.example.ticketingsystem.repository;

import com.example.ticketingsystem.mapper.PaymentRowMapper;
import com.example.ticketingsystem.model.Payment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Optional;

@Repository
public class PaymentDAO {

    private final JdbcTemplate jdbcTemplate;

    public PaymentDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Payment create(Payment payment) {
        String query = """
                INSERT INTO payments (order_id, external_payment_id, amount, status, paid_at)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setLong(1, payment.getOrderId());
            ps.setString(2, payment.getExternalPaymentId());
            ps.setBigDecimal(3, payment.getAmount());
            ps.setString(4, payment.getStatus());
            ps.setObject(5, payment.getPaidAt());
            return ps;
        }, keyHolder);

        payment.setId(keyHolder.getKey().longValue());
        return payment;
    }

    public Optional<Payment> findByOrderId(Long orderId) {
        String query = """
                SELECT id, order_id, external_payment_id, amount, status, paid_at
                FROM payments
                WHERE order_id = ?
                """;
        var results = jdbcTemplate.query(query, new PaymentRowMapper(), orderId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    public Payment updateStatus(Long id, String status) {
        String query = """
                UPDATE payments
                SET status = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(query, status, id);
        String selectQuery = """
                SELECT id, order_id, external_payment_id, amount, status, paid_at
                FROM payments
                WHERE id = ?
                """;
        return jdbcTemplate.queryForObject(selectQuery, new PaymentRowMapper(), id);
    }

    public Payment updatePayment(Long id, String status, String externalPaymentId, java.time.LocalDateTime paidAt) {
        String query = """
                UPDATE payments
                SET status = ?, external_payment_id = ?, paid_at = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(query, status, externalPaymentId, paidAt, id);
        String selectQuery = """
                SELECT id, order_id, external_payment_id, amount, status, paid_at
                FROM payments
                WHERE id = ?
                """;
        return jdbcTemplate.queryForObject(selectQuery, new PaymentRowMapper(), id);
    }
}
