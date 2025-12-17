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

    public int[] batchUpdateStatus(List<Long> ticketIds, String newStatus) {
        String query = "UPDATE tickets SET status = ? WHERE id = ?";

        return jdbcTemplate.batchUpdate(query, ticketIds, ticketIds.size(),
                (PreparedStatement ps, Long ticketId) -> {
                    ps.setString(1, newStatus);
                    ps.setLong(2, ticketId);
                });
    }

    public List<Ticket> findByOrderItemId(Long orderItemId) {
        String query = """
                SELECT id, ticket_code, order_item_id, attendee_name, attendee_email, status
                FROM tickets
                WHERE order_item_id = ?
                """;
        return jdbcTemplate.query(query, new TicketRowMapper(), orderItemId);
    }

    public List<Ticket> findByEventId(Long eventId) {
        String query = """
                SELECT t.id, t.ticket_code, t.order_item_id, t.attendee_name, t.attendee_email, t.status
                FROM tickets t
                JOIN order_items oi ON t.order_item_id = oi.id
                JOIN ticket_categories tc ON oi.ticket_category_id = tc.id
                WHERE tc.event_id = ?
                """;
        return jdbcTemplate.query(query, new TicketRowMapper(), eventId);
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
