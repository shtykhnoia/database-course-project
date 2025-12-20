package com.example.ticketingsystem.repository;

import com.example.ticketingsystem.mapper.EventSalesViewRowMapper;
import com.example.ticketingsystem.mapper.PromoCodeEffectivenessViewRowMapper;
import com.example.ticketingsystem.mapper.VenueUtilizationViewRowMapper;
import com.example.ticketingsystem.model.EventSalesView;
import com.example.ticketingsystem.model.PromoCodeEffectivenessView;
import com.example.ticketingsystem.model.VenueUtilizationView;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StatisticsDAO {

    private final JdbcTemplate jdbcTemplate;

    public StatisticsDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<EventSalesView> getEventSales() {
        String query = "SELECT * FROM event_sales_view ORDER BY event_id, category_id";
        return jdbcTemplate.query(query, new EventSalesViewRowMapper());
    }

    public List<EventSalesView> getEventSalesByEventId(Long eventId) {
        String query = "SELECT * FROM event_sales_view WHERE event_id = ? ORDER BY category_id";
        return jdbcTemplate.query(query, new EventSalesViewRowMapper(), eventId);
    }

    public List<VenueUtilizationView> getVenueUtilization() {
        String query = "SELECT * FROM venue_utilization_view";
        return jdbcTemplate.query(query, new VenueUtilizationViewRowMapper());
    }

    public VenueUtilizationView getVenueUtilizationById(Long venueId) {
        String query = "SELECT * FROM venue_utilization_view WHERE venue_id = ?";
        List<VenueUtilizationView> results = jdbcTemplate.query(query, new VenueUtilizationViewRowMapper(), venueId);
        return results.isEmpty() ? null : results.getFirst();
    }

    public List<PromoCodeEffectivenessView> getPromoCodeEffectiveness() {
        String query = "SELECT * FROM promo_code_effectiveness_view";
        return jdbcTemplate.query(query, new PromoCodeEffectivenessViewRowMapper());
    }

    public PromoCodeEffectivenessView getPromoCodeEffectivenessById(Long promoCodeId) {
        String query = "SELECT * FROM promo_code_effectiveness_view WHERE promo_code_id = ?";
        List<PromoCodeEffectivenessView> results = jdbcTemplate.query(query, new PromoCodeEffectivenessViewRowMapper(), promoCodeId);
        return results.isEmpty() ? null : results.getFirst();
    }
}
