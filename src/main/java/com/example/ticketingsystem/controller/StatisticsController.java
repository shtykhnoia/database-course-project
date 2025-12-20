package com.example.ticketingsystem.controller;

import com.example.ticketingsystem.model.EventSalesView;
import com.example.ticketingsystem.model.VenueUtilizationView;
import com.example.ticketingsystem.model.PromoCodeEffectivenessView;
import com.example.ticketingsystem.service.StatisticsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@AllArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/event-sales")
    public ResponseEntity<List<EventSalesView>> getEventSales(
            @RequestParam(required = false) Long eventId) {
        if (eventId != null) {
            return ResponseEntity.ok(statisticsService.getEventSalesByEventId(eventId));
        }
        return ResponseEntity.ok(statisticsService.getEventSales());
    }

    @GetMapping("/venue-utilization")
    public ResponseEntity<List<VenueUtilizationView>> getVenueUtilization() {
        return ResponseEntity.ok(statisticsService.getVenueUtilization());
    }

    @GetMapping("/venue-utilization/{venueId}")
    public ResponseEntity<VenueUtilizationView> getVenueUtilizationById(@PathVariable Long venueId) {
        VenueUtilizationView stats = statisticsService.getVenueUtilizationById(venueId);
        return stats != null ? ResponseEntity.ok(stats) : ResponseEntity.notFound().build();
    }

    @GetMapping("/promo-code-effectiveness")
    public ResponseEntity<List<PromoCodeEffectivenessView>> getPromoCodeEffectiveness() {
        return ResponseEntity.ok(statisticsService.getPromoCodeEffectiveness());
    }

    @GetMapping("/promo-code-effectiveness/{promoCodeId}")
    public ResponseEntity<PromoCodeEffectivenessView> getPromoCodeEffectivenessById(@PathVariable Long promoCodeId) {
        PromoCodeEffectivenessView stats = statisticsService.getPromoCodeEffectivenessById(promoCodeId);
        return stats != null ? ResponseEntity.ok(stats) : ResponseEntity.notFound().build();
    }
}
