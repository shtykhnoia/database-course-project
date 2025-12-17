package com.example.ticketingsystem.repository;

import com.example.ticketingsystem.mapper.EventSalesViewRowMapper;
import com.example.ticketingsystem.mapper.OrganizerPerformanceViewRowMapper;
import com.example.ticketingsystem.mapper.PromoCodeEffectivenessViewRowMapper;
import com.example.ticketingsystem.model.EventSalesView;
import com.example.ticketingsystem.model.OrganizerPerformanceView;
import com.example.ticketingsystem.model.PromoCodeEffectivenessView;
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

    public List<OrganizerPerformanceView> getOrganizerPerformance() {
        String query = "SELECT * FROM organizer_performance_view ORDER BY total_revenue DESC";
        return jdbcTemplate.query(query, new OrganizerPerformanceViewRowMapper());
    }

    public OrganizerPerformanceView getOrganizerPerformanceById(Long organizerId) {
        String query = "SELECT * FROM organizer_performance_view WHERE organizer_id = ?";
        List<OrganizerPerformanceView> results = jdbcTemplate.query(query, new OrganizerPerformanceViewRowMapper(), organizerId);
        return results.isEmpty() ? null : results.getFirst();
    }

    public List<PromoCodeEffectivenessView> getPromoCodeEffectiveness() {
        String query = "SELECT * FROM promo_code_effectiveness_view ORDER BY orders_with_promo DESC";
        return jdbcTemplate.query(query, new PromoCodeEffectivenessViewRowMapper());
    }

    public PromoCodeEffectivenessView getPromoCodeEffectivenessById(Long promoCodeId) {
        String query = "SELECT * FROM promo_code_effectiveness_view WHERE promo_code_id = ?";
        List<PromoCodeEffectivenessView> results = jdbcTemplate.query(query, new PromoCodeEffectivenessViewRowMapper(), promoCodeId);
        return results.isEmpty() ? null : results.getFirst();
    }
}
