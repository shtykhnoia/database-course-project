package com.example.ticketingsystem.repository;

import com.example.ticketingsystem.mapper.TicketRowMapper;
import com.example.ticketingsystem.model.Ticket;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class TicketDAO {

    private final JdbcTemplate jdbcTemplate;

    public TicketDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Ticket create(Ticket ticket) {
        String query = """
                INSERT INTO tickets (ticket_code, order_item_id, attendee_name, attendee_email, status)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, ticket.getTicketCode());
            ps.setLong(2, ticket.getOrderItemId());
            ps.setString(3, ticket.getAttendeeName());
            ps.setString(4, ticket.getAttendeeEmail());
            ps.setString(5, ticket.getStatus());
            return ps;
        }, keyHolder);

        ticket.setId(keyHolder.getKey().longValue());
        return ticket;
    }

    public List<Ticket> findByOrderItemId(Long orderItemId) {
        String query = """
                SELECT id, ticket_code, order_item_id, attendee_name, attendee_email, status
                FROM tickets
                WHERE order_item_id = ?
                """;
        return jdbcTemplate.query(query, new TicketRowMapper(), orderItemId);
    }

    public Optional<Ticket> findByTicketCode(String ticketCode) {
        String query = """
                SELECT id, ticket_code, order_item_id, attendee_name, attendee_email, status
                FROM tickets
                WHERE ticket_code = ?
                """;
        var results = jdbcTemplate.query(query, new TicketRowMapper(), ticketCode);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    public Ticket updateStatus(Long id, String status) {
        String query = """
                UPDATE tickets
                SET status = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(query, status, id);
        String selectQuery = """
                SELECT id, ticket_code, order_item_id, attendee_name, attendee_email, status
                FROM tickets
                WHERE id = ?
                """;
        return jdbcTemplate.queryForObject(selectQuery, new TicketRowMapper(), id);
    }
}
