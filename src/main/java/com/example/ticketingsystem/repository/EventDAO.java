package com.example.ticketingsystem.repository;

import com.example.ticketingsystem.mapper.EventRowMapper;
import com.example.ticketingsystem.model.Event;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class EventDAO {

    private final JdbcTemplate jdbcTemplate;

    public EventDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Event> getAllEvents() {
        String query = """
                SELECT id,
                title,
                description,
                organizer_id,
                start_datetime,
                event_status
                FROM events
                """;
        return jdbcTemplate.query(query, new EventRowMapper());
    }

    public Optional<Event> getEventById(Long id) {
        String query = """
                SELECT id,
                title,
                description,
                organizer_id,
                start_datetime,
                event_status
                FROM events
                WHERE id=?
                """;
        List<Event> events = jdbcTemplate.query(query, new EventRowMapper(), id);
        return events.isEmpty() ? Optional.empty() : Optional.of(events.getFirst());
    }

    public Event createEvent(Event event) {
        String query = """
                INSERT INTO events (title, description, organizer_id, start_datetime, event_status)
                VALUES(?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(query, new String[]{"id"});
            ps.setString(1, event.getTitle());
            ps.setString(2, event.getDescription());
            ps.setLong(3, event.getOrganizerId());
            ps.setObject(4, event.getStartDatetime());
            ps.setString(5, event.getEventStatus());
            return ps;
        }, keyHolder);
        event.setId(keyHolder.getKey().longValue());
        return event;
    }

    public Event updateEvent(Event event) {
        String query = """
                UPDATE events
                SET title = ?,
                    description = ?,
                    organizer_id = ?,
                    start_datetime = ?,
                    event_status = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(query,
                event.getTitle(),
                event.getDescription(),
                event.getOrganizerId(),
                event.getStartDatetime(),
                event.getEventStatus(),
                event.getId());
        return event;
    }

    public void updateEventStatus(Long eventId, String status) {
        String query = """
                UPDATE events
                SET event_status = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(query, status, eventId);
    }

    public void deleteEvent(Long id) {
        String query = "DELETE FROM events WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    public List<Event> getEventsByOrganizerId(Long organizerId) {
        String query = """
                SELECT id, title, description, organizer_id, start_datetime, event_status
                FROM events
                WHERE organizer_id = ?
                ORDER BY start_datetime DESC
                """;
        return jdbcTemplate.query(query, new EventRowMapper(), organizerId);
    }

    public List<Event> getEventsByStatus(String status) {
        String query = """
                SELECT id, title, description, organizer_id, start_datetime, event_status
                FROM events
                WHERE event_status = ?
                ORDER BY start_datetime DESC
                """;
        return jdbcTemplate.query(query, new EventRowMapper(), status);
    }
}
