package com.example.ticketingsystem.mapper;

import com.example.ticketingsystem.model.PromoCode;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PromoCodeRowMapper implements RowMapper<PromoCode> {
    @Override
    public PromoCode mapRow(ResultSet rs, int rowNum) throws SQLException {
        PromoCode promoCode = new PromoCode();
        promoCode.setId(rs.getLong("id"));
        promoCode.setCode(rs.getString("code"));
        promoCode.setEventId(rs.getObject("event_id", Long.class));
        promoCode.setDiscountType(rs.getString("discount_type"));
        promoCode.setDiscountValue(rs.getBigDecimal("discount_value"));
        promoCode.setMaxUses(rs.getObject("max_uses", Integer.class));
        promoCode.setUsedCount(rs.getInt("used_count"));
        promoCode.setValidFrom(rs.getObject("valid_from", java.time.LocalDateTime.class));
        promoCode.setValidUntil(rs.getObject("valid_until", java.time.LocalDateTime.class));
        return promoCode;
    }
}
