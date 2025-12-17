package com.example.ticketingsystem.mapper;

import com.example.ticketingsystem.model.EventTag;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventTagRowMapper implements RowMapper<EventTag> {
    @Override
    public EventTag mapRow(ResultSet rs, int rowNum) throws SQLException {
        EventTag eventTag = new EventTag();
        eventTag.setId(rs.getLong("id"));
        eventTag.setName(rs.getString("name"));
        return eventTag;
    }
}
