package com.example.ticketingsystem.mapper;

import com.example.ticketingsystem.model.PromoCodeEffectivenessView;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PromoCodeEffectivenessViewRowMapper implements RowMapper<PromoCodeEffectivenessView> {
    @Override
    public PromoCodeEffectivenessView mapRow(ResultSet rs, int rowNum) throws SQLException {
        PromoCodeEffectivenessView view = new PromoCodeEffectivenessView();
        view.setPromoCodeId(rs.getLong("promo_code_id"));
        view.setCode(rs.getString("code"));
        view.setEventId(rs.getObject("event_id", Long.class));
        view.setEventTitle(rs.getString("event_title"));
        view.setDiscountType(rs.getString("discount_type"));
        view.setDiscountValue(rs.getBigDecimal("discount_value"));
        view.setOrdersWithPromo(rs.getInt("orders_with_promo"));
        view.setTotalDiscountGiven(rs.getBigDecimal("total_discount_given"));
        view.setAvgDiscountPerOrder(rs.getBigDecimal("avg_discount_per_order"));
        return view;
    }
}
