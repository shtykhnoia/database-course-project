package com.example.ticketingsystem.repository;

import com.example.ticketingsystem.mapper.PromoCodeRowMapper;
import com.example.ticketingsystem.model.PromoCode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class PromoCodeDAO {
    private final JdbcTemplate jdbcTemplate;

    public PromoCodeDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PromoCode createPromoCode(PromoCode promoCode) {
        String query = """
                INSERT INTO promo_codes(code, event_id, discount_type, discount_value, max_uses, used_count, valid_from, valid_until)
                VALUES(?, ?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(query, new String[]{"id"});
            ps.setString(1, promoCode.getCode());
            ps.setObject(2, promoCode.getEventId());
            ps.setString(3, promoCode.getDiscountType());
            ps.setBigDecimal(4, promoCode.getDiscountValue());
            ps.setObject(5, promoCode.getMaxUses());
            ps.setInt(6, promoCode.getUsedCount());
            ps.setObject(7, promoCode.getValidFrom());
            ps.setObject(8, promoCode.getValidUntil());
            return ps;
        }, keyHolder);
        promoCode.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return promoCode;
    }

    public Optional<PromoCode> findById(Long id) {
        String query = """
                SELECT id, code, event_id, discount_type, discount_value, max_uses, used_count, valid_from, valid_until
                FROM promo_codes
                WHERE id = ?
                """;
        List<PromoCode> promoCodes = jdbcTemplate.query(query, new PromoCodeRowMapper(), id);
        return promoCodes.isEmpty() ? Optional.empty() : Optional.of(promoCodes.getFirst());
    }

    public Optional<PromoCode> findByCode(String code) {
        String query = """
                SELECT id, code, event_id, discount_type, discount_value, max_uses, used_count, valid_from, valid_until
                FROM promo_codes
                WHERE code = ?
                """;
        List<PromoCode> promoCodes = jdbcTemplate.query(query, new PromoCodeRowMapper(), code);
        return promoCodes.isEmpty() ? Optional.empty() : Optional.of(promoCodes.getFirst());
    }

    public List<PromoCode> findByEventId(Long eventId) {
        String query = """
                SELECT id, code, event_id, discount_type, discount_value, max_uses, used_count, valid_from, valid_until
                FROM promo_codes
                WHERE event_id = ?
                """;
        return jdbcTemplate.query(query, new PromoCodeRowMapper(), eventId);
    }

    public int incrementUsedCount(Long id) {
        String query = """
                UPDATE promo_codes
                SET used_count = used_count + 1
                WHERE id = ? AND (max_uses IS NULL OR used_count < max_uses);
                """;
        return jdbcTemplate.update(query, id);
    }

    public int deletePromoCode(Long id) {
        String query = """
                DELETE FROM promo_codes
                WHERE id = ?
                """;
        return jdbcTemplate.update(query, id);
    }

    public List<PromoCode> getAllPromoCodes() {
        String query = """
                SELECT id, code, event_id, discount_type, discount_value, max_uses, used_count, valid_from, valid_until
                FROM promo_codes
                """;
        return jdbcTemplate.query(query, new PromoCodeRowMapper());
    }
}
