package com.example.ticketingsystem.mapper;

import com.example.ticketingsystem.model.Ticket;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TicketRowMapper implements RowMapper<Ticket> {
    @Override
    public Ticket mapRow(ResultSet rs, int rowNum) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getLong("id"));
        ticket.setTicketCode(rs.getString("ticket_code"));
        ticket.setOrderItemId(rs.getLong("order_item_id"));
        ticket.setAttendeeName(rs.getString("attendee_name"));
        ticket.setAttendeeEmail(rs.getString("attendee_email"));
        ticket.setStatus(rs.getString("status"));
        return ticket;
    }
}
