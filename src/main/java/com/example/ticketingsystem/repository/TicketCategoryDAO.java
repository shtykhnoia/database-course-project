package com.example.ticketingsystem.repository;

import com.example.ticketingsystem.mapper.TicketCategoryRowMapper;
import com.example.ticketingsystem.model.TicketCategory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class TicketCategoryDAO {

    private final JdbcTemplate jdbcTemplate;

    public TicketCategoryDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TicketCategory> findByEventId(Long eventId) {
        String query = """
                SELECT id, event_id, name, description, price, quantity_available,
                       sale_start_date, sale_end_date
                FROM ticket_categories
                WHERE event_id = ?
                ORDER BY price
                """;
        return jdbcTemplate.query(query, new TicketCategoryRowMapper(), eventId);
    }

    public Optional<TicketCategory> findById(Long id) {
        String query = """
                SELECT id, event_id, name, description, price, quantity_available,
                       sale_start_date, sale_end_date
                FROM ticket_categories
                WHERE id = ?
                """;
        List<TicketCategory> results = jdbcTemplate.query(query, new TicketCategoryRowMapper(), id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    public TicketCategory create(TicketCategory ticketCategory) {
        String query = """
                INSERT INTO ticket_categories (event_id, name, description, price,
                                              quantity_available, sale_start_date, sale_end_date)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setLong(1, ticketCategory.getEventId());
            ps.setString(2, ticketCategory.getName());
            ps.setString(3, ticketCategory.getDescription());
            ps.setBigDecimal(4, ticketCategory.getPrice());
            ps.setInt(5, ticketCategory.getQuantityAvailable());
            ps.setObject(6, ticketCategory.getSaleStartDate());
            ps.setObject(7, ticketCategory.getSaleEndDate());
            return ps;
        }, keyHolder);

        ticketCategory.setId(keyHolder.getKey().longValue());
        return ticketCategory;
    }

    public TicketCategory update(TicketCategory ticketCategory) {
        String query = """
                UPDATE ticket_categories
                SET name = ?,
                    description = ?,
                    price = ?,
                    quantity_available = ?,
                    sale_start_date = ?,
                    sale_end_date = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(query,
                ticketCategory.getName(),
                ticketCategory.getDescription(),
                ticketCategory.getPrice(),
                ticketCategory.getQuantityAvailable(),
                ticketCategory.getSaleStartDate(),
                ticketCategory.getSaleEndDate(),
                ticketCategory.getId());

        return ticketCategory;
    }

    public void delete(Long id) {
        String query = "DELETE FROM ticket_categories WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    public int decreaseQuantity(Long id, int quantity) {
        String query = """
                UPDATE ticket_categories
                SET quantity_available = quantity_available - ?
                WHERE id = ? AND quantity_available >= ?
                """;
        return jdbcTemplate.update(query, quantity, id, quantity);
    }

    public int increaseQuantity(Long id, int quantity) {
        String query = """
                UPDATE ticket_categories
                SET quantity_available = quantity_available + ?
                WHERE id = ?
                """;
        return jdbcTemplate.update(query, quantity, id);
    }
}
