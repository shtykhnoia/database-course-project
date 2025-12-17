package com.example.ticketingsystem.mapper;

import com.example.ticketingsystem.model.EventSalesView;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventSalesViewRowMapper implements RowMapper<EventSalesView> {
    @Override
    public EventSalesView mapRow(ResultSet rs, int rowNum) throws SQLException {
        EventSalesView view = new EventSalesView();
        view.setEventId(rs.getLong("event_id"));
        view.setEventTitle(rs.getString("event_title"));
        view.setCategoryId(rs.getLong("category_id"));
        view.setCategoryName(rs.getString("category_name"));
        view.setCategoryPrice(rs.getBigDecimal("category_price"));
        view.setTotalSold(rs.getInt("total_sold"));
        view.setQuantityAvailable(rs.getInt("quantity_available"));
        view.setTotalRevenue(rs.getBigDecimal("total_revenue"));
        view.setSoldLast7Days(rs.getInt("sold_last_7_days"));
        return view;
    }
}
