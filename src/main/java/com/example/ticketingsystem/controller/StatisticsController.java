package com.example.ticketingsystem.controller;

import com.example.ticketingsystem.model.EventSalesView;
import com.example.ticketingsystem.model.VenueUtilizationView;
import com.example.ticketingsystem.model.PromoCodeEffectivenessView;
import com.example.ticketingsystem.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@AllArgsConstructor
@Tag(name = "Статистика", description = "Аналитические представления данных о продажах, площадках и промокодах")
@SecurityRequirement(name = "bearerAuth")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/event-sales")
    @Operation(summary = "Статистика продаж по мероприятиям",
               description = "Возвращает статистику продаж билетов по категориям. Можно фильтровать по конкретному мероприятию")
    public ResponseEntity<List<EventSalesView>> getEventSales(
            @Parameter(description = "ID мероприятия (опционально)") @RequestParam(required = false) Long eventId) {
        if (eventId != null) {
            return ResponseEntity.ok(statisticsService.getEventSalesByEventId(eventId));
        }
        return ResponseEntity.ok(statisticsService.getEventSales());
    }

    @GetMapping("/venue-utilization")
    @Operation(summary = "Статистика использования площадок",
               description = "Возвращает данные о загрузке площадок, количестве мероприятий и выручке")
    public ResponseEntity<List<VenueUtilizationView>> getVenueUtilization() {
        return ResponseEntity.ok(statisticsService.getVenueUtilization());
    }

    @GetMapping("/venue-utilization/{venueId}")
    public ResponseEntity<VenueUtilizationView> getVenueUtilizationById(@PathVariable Long venueId) {
        VenueUtilizationView stats = statisticsService.getVenueUtilizationById(venueId);
        return stats != null ? ResponseEntity.ok(stats) : ResponseEntity.notFound().build();
    }

    @GetMapping("/promo-code-effectiveness")
    @Operation(summary = "Эффективность промокодов",
               description = "Показывает статистику использования промокодов и сумму предоставленных скидок")
    public ResponseEntity<List<PromoCodeEffectivenessView>> getPromoCodeEffectiveness() {
        return ResponseEntity.ok(statisticsService.getPromoCodeEffectiveness());
    }

    @GetMapping("/promo-code-effectiveness/{promoCodeId}")
    public ResponseEntity<PromoCodeEffectivenessView> getPromoCodeEffectivenessById(@PathVariable Long promoCodeId) {
        PromoCodeEffectivenessView stats = statisticsService.getPromoCodeEffectivenessById(promoCodeId);
        return stats != null ? ResponseEntity.ok(stats) : ResponseEntity.notFound().build();
    }
}
