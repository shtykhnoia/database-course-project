package com.example.ticketingsystem.mapper;

import com.example.ticketingsystem.model.VenueUtilizationView;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VenueUtilizationViewRowMapper implements RowMapper<VenueUtilizationView> {
    @Override
    public VenueUtilizationView mapRow(ResultSet rs, int rowNum) throws SQLException {
        VenueUtilizationView view = new VenueUtilizationView();
        view.setVenueId(rs.getLong("venue_id"));
        view.setVenueName(rs.getString("venue_name"));
        view.setAddress(rs.getString("address"));
        view.setCapacity(rs.getObject("capacity", Integer.class));
        view.setTotalEvents(rs.getLong("total_events"));
        view.setActiveEvents(rs.getLong("active_events"));
        view.setTicketsSold(rs.getLong("tickets_sold"));
        view.setTotalRevenue(rs.getBigDecimal("total_revenue"));
        view.setAvgOccupancyPercentage(rs.getObject("avg_occupancy_percentage", Integer.class));
        return view;
    }
}
