package com.example.ticketingsystem.mapper;

import com.example.ticketingsystem.model.Event;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        Event event = new Event();
        event.setId(rs.getLong("id"));
        event.setTitle(rs.getString("title"));
        event.setDescription(rs.getString("description"));
        event.setOrganizerId(rs.getLong("organizer_id"));
        event.setStartDatetime(rs.getObject("start_datetime", java.time.LocalDateTime.class));
        event.setEventStatus(rs.getString("event_status"));
        return event;
    }
}
