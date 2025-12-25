package com.example.ticketingsystem.repository;

import com.example.ticketingsystem.mapper.EventTagRowMapper;
import com.example.ticketingsystem.model.EventTag;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class EventTagDAO {

    private final JdbcTemplate jdbcTemplate;

    public EventTagDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public EventTag create(EventTag eventTag) {
        String query = """
                INSERT INTO event_tags (name)
                VALUES (?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, eventTag.getName());
            return ps;
        }, keyHolder);

        eventTag.setId(keyHolder.getKey().longValue());
        return eventTag;
    }

    public Optional<EventTag> findById(Long id) {
        String query = """
                SELECT id, name
                FROM event_tags
                WHERE id = ?
                """;
        List<EventTag> results = jdbcTemplate.query(query, new EventTagRowMapper(), id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    public Optional<EventTag> findByName(String name) {
        String query = """
                SELECT id, name
                FROM event_tags
                WHERE name = ?
                """;
        List<EventTag> results = jdbcTemplate.query(query, new EventTagRowMapper(), name);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    public List<EventTag> findAll() {
        String query = """
                SELECT id, name
                FROM event_tags
                ORDER BY name
                """;
        return jdbcTemplate.query(query, new EventTagRowMapper());
    }

    public List<EventTag> findByEventId(Long eventId) {
        String query = """
                SELECT et.id, et.name
                FROM event_tags et
                JOIN event_tag_assignments eta ON et.id = eta.tag_id
                WHERE eta.event_id = ?
                ORDER BY et.name
                """;
        return jdbcTemplate.query(query, new EventTagRowMapper(), eventId);
    }

    public int delete(Long id) {
        String query = "DELETE FROM event_tags WHERE id = ?";
        return jdbcTemplate.update(query, id);
    }

    public void assignTagToEvent(Long eventId, Long tagId) {
        String query = """
                INSERT INTO event_tag_assignments (event_id, tag_id)
                VALUES (?, ?)
                ON CONFLICT DO NOTHING
                """;
        jdbcTemplate.update(query, eventId, tagId);
    }

    public int removeTagFromEvent(Long eventId, Long tagId) {
        String query = """
                DELETE FROM event_tag_assignments
                WHERE event_id = ? AND tag_id = ?
                """;
        return jdbcTemplate.update(query, eventId, tagId);
    }

}
