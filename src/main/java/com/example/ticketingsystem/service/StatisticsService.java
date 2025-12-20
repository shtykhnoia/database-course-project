package com.example.ticketingsystem.service;

import com.example.ticketingsystem.model.EventSalesView;
import com.example.ticketingsystem.model.VenueUtilizationView;
import com.example.ticketingsystem.model.PromoCodeEffectivenessView;
import com.example.ticketingsystem.repository.StatisticsDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class StatisticsService {

    private final StatisticsDAO statisticsDAO;

    public List<EventSalesView> getEventSales() {
        return statisticsDAO.getEventSales();
    }

    public List<EventSalesView> getEventSalesByEventId(Long eventId) {
        return statisticsDAO.getEventSalesByEventId(eventId);
    }

    public List<VenueUtilizationView> getVenueUtilization() {
        return statisticsDAO.getVenueUtilization();
    }

    public VenueUtilizationView getVenueUtilizationById(Long venueId) {
        return statisticsDAO.getVenueUtilizationById(venueId);
    }

    public List<PromoCodeEffectivenessView> getPromoCodeEffectiveness() {
        return statisticsDAO.getPromoCodeEffectiveness();
    }

    public PromoCodeEffectivenessView getPromoCodeEffectivenessById(Long promoCodeId) {
        return statisticsDAO.getPromoCodeEffectivenessById(promoCodeId);
    }
}
