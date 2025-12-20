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
        view.setTicketsSold(rs.getInt("tickets_sold"));
        view.setTicketsAvailable(rs.getInt("tickets_available"));
        view.setAvgTicketPrice(rs.getBigDecimal("avg_ticket_price"));
        view.setTicketsSoldLast7Days(rs.getInt("tickets_sold_last_7_days"));
        view.setSoldPercentage(rs.getInt("sold_percentage"));
        return view;
    }
}
