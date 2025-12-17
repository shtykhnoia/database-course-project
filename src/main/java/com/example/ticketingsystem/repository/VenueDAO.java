package com.example.ticketingsystem.repository;

import com.example.ticketingsystem.mapper.VenueRowMapper;
import com.example.ticketingsystem.model.Venue;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class VenueDAO {

    private final JdbcTemplate jdbcTemplate;

    public VenueDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Venue create(Venue venue) {
        String query = """
                INSERT INTO venues (name, address, capacity)
                VALUES (?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, venue.getName());
            ps.setString(2, venue.getAddress());
            ps.setObject(3, venue.getCapacity());
            return ps;
        }, keyHolder);

        venue.setId(keyHolder.getKey().longValue());
        return venue;
    }

    public Optional<Venue> findById(Long id) {
        String query = """
                SELECT id, name, address, capacity
                FROM venues
                WHERE id = ?
                """;
        List<Venue> results = jdbcTemplate.query(query, new VenueRowMapper(), id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    public List<Venue> findAll() {
        String query = """
                SELECT id, name, address, capacity
                FROM venues
                ORDER BY name
                """;
        return jdbcTemplate.query(query, new VenueRowMapper());
    }

    public Venue update(Venue venue) {
        String query = """
                UPDATE venues
                SET name = ?,
                    address = ?,
                    capacity = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(query,
                venue.getName(),
                venue.getAddress(),
                venue.getCapacity(),
                venue.getId());
        return venue;
    }

    public int delete(Long id) {
        String query = "DELETE FROM venues WHERE id = ?";
        return jdbcTemplate.update(query, id);
    }
}
