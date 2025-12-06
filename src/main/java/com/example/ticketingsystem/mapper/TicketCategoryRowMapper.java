package com.example.ticketingsystem.mapper;

import com.example.ticketingsystem.model.TicketCategory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TicketCategoryRowMapper implements RowMapper<TicketCategory> {
    @Override
    public TicketCategory mapRow(ResultSet rs, int rowNum) throws SQLException {
        TicketCategory ticketCategory = new TicketCategory();
        ticketCategory.setId(rs.getLong("id"));
        ticketCategory.setEventId(rs.getLong("event_id"));
        ticketCategory.setName(rs.getString("name"));
        ticketCategory.setDescription(rs.getString("description"));
        ticketCategory.setPrice(rs.getBigDecimal("price"));
        ticketCategory.setQuantityAvailable(rs.getInt("quantity_available"));
        ticketCategory.setSaleStartDate(rs.getObject("sale_start_date", java.time.LocalDateTime.class));
        ticketCategory.setSaleEndDate(rs.getObject("sale_end_date", java.time.LocalDateTime.class));
        return ticketCategory;
    }
}
