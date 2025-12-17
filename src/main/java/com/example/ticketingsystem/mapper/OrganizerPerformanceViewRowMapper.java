package com.example.ticketingsystem.mapper;

import com.example.ticketingsystem.model.OrganizerPerformanceView;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrganizerPerformanceViewRowMapper implements RowMapper<OrganizerPerformanceView> {
    @Override
    public OrganizerPerformanceView mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrganizerPerformanceView view = new OrganizerPerformanceView();
        view.setOrganizerId(rs.getLong("organizer_id"));
        view.setOrganizerName(rs.getString("organizer_name"));
        view.setTotalEvents(rs.getInt("total_events"));
        view.setPublishedEvents(rs.getInt("published_events"));
        view.setCancelledEvents(rs.getInt("cancelled_events"));
        view.setTotalTicketsSold(rs.getInt("total_tickets_sold"));
        view.setTotalRevenue(rs.getBigDecimal("total_revenue"));
        view.setAvgTicketPrice(rs.getBigDecimal("avg_ticket_price"));
        view.setSuccessRate(rs.getBigDecimal("success_rate"));
        return view;
    }
}
