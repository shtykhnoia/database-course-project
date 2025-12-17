package com.example.ticketingsystem.mapper;

import com.example.ticketingsystem.model.Venue;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VenueRowMapper implements RowMapper<Venue> {
    @Override
    public Venue mapRow(ResultSet rs, int rowNum) throws SQLException {
        Venue venue = new Venue();
        venue.setId(rs.getLong("id"));
        venue.setName(rs.getString("name"));
        venue.setAddress(rs.getString("address"));
        venue.setCapacity(rs.getObject("capacity", Integer.class));
        return venue;
    }
}
