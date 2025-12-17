package com.example.ticketingsystem.mapper;

import com.example.ticketingsystem.model.Organizer;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrganizerRowMapper implements RowMapper<Organizer> {
    @Override
    public Organizer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Organizer organizer = new Organizer();
        organizer.setId(rs.getLong("id"));
        organizer.setName(rs.getString("name"));
        organizer.setDescription(rs.getString("description"));
        organizer.setContactEmail(rs.getString("contact_email"));
        organizer.setContactPhone(rs.getString("contact_phone"));
        organizer.setUserId(rs.getObject("user_id", Long.class));
        return organizer;
    }
}
