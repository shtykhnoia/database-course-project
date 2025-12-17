package com.example.ticketingsystem.repository;

import com.example.ticketingsystem.mapper.OrganizerRowMapper;
import com.example.ticketingsystem.model.Organizer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class OrganizerDAO {

    private final JdbcTemplate jdbcTemplate;

    public OrganizerDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Organizer> findAll() {
        String query = """
                SELECT id, name, description, contact_email, contact_phone, user_id
                FROM organizers
                ORDER BY name
                """;
        return jdbcTemplate.query(query, new OrganizerRowMapper());
    }

    public Optional<Organizer> findById(Long id) {
        String query = """
                SELECT id, name, description, contact_email, contact_phone, user_id
                FROM organizers
                WHERE id = ?
                """;
        List<Organizer> results = jdbcTemplate.query(query, new OrganizerRowMapper(), id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    public Optional<Organizer> findByUserId(Long userId) {
        String query = """
                SELECT id, name, description, contact_email, contact_phone, user_id
                FROM organizers
                WHERE user_id = ?
                """;
        List<Organizer> results = jdbcTemplate.query(query, new OrganizerRowMapper(), userId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    public Organizer create(Organizer organizer) {
        String query = """
                INSERT INTO organizers (name, description, contact_email, contact_phone, user_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, organizer.getName());
            ps.setString(2, organizer.getDescription());
            ps.setString(3, organizer.getContactEmail());
            ps.setString(4, organizer.getContactPhone());
            ps.setObject(5, organizer.getUserId());
            return ps;
        }, keyHolder);

        organizer.setId(keyHolder.getKey().longValue());
        return organizer;
    }

    public Organizer update(Organizer organizer) {
        String query = """
                UPDATE organizers
                SET name = ?,
                    description = ?,
                    contact_email = ?,
                    contact_phone = ?,
                    user_id = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(query,
                organizer.getName(),
                organizer.getDescription(),
                organizer.getContactEmail(),
                organizer.getContactPhone(),
                organizer.getUserId(),
                organizer.getId());

        return organizer;
    }

    public void delete(Long id) {
        String query = "DELETE FROM organizers WHERE id = ?";
        jdbcTemplate.update(query, id);
    }
}
