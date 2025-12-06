package com.example.ticketingsystem.mapper;

import com.example.ticketingsystem.model.Payment;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentRowMapper implements RowMapper<Payment> {
    @Override
    public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getLong("id"));
        payment.setOrderId(rs.getLong("order_id"));
        payment.setExternalPaymentId(rs.getString("external_payment_id"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setStatus(rs.getString("status"));
        payment.setPaidAt(rs.getObject("paid_at", java.time.LocalDateTime.class));
        return payment;
    }
}
