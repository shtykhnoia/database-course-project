package com.example.ticketingsystem.service;

import com.example.ticketingsystem.exception.ResourceNotFoundException;
import com.example.ticketingsystem.model.Event;
import com.example.ticketingsystem.model.TicketCategory;
import com.example.ticketingsystem.repository.EventDAO;
import com.example.ticketingsystem.repository.TicketCategoryDAO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketCategoryService {

    private final TicketCategoryDAO ticketCategoryDAO;
    private final EventDAO eventDAO;

    public TicketCategoryService(TicketCategoryDAO ticketCategoryDAO, EventDAO eventDAO) {
        this.ticketCategoryDAO = ticketCategoryDAO;
        this.eventDAO = eventDAO;
    }

    public List<TicketCategory> getTicketCategoriesByEventId(Long eventId) {
        eventDAO.getEventById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));
        return ticketCategoryDAO.findByEventId(eventId);
    }

    public Optional<TicketCategory> getTicketCategoryById(Long id) {
        return ticketCategoryDAO.findById(id);
    }

    public TicketCategory createTicketCategory(Long eventId, TicketCategory ticketCategory) {
        Event event = eventDAO.getEventById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));

        if ("cancelled".equals(event.getEventStatus())) {
            throw new IllegalArgumentException("Cannot create ticket category for cancelled event");
        }

        validateSaleDates(ticketCategory.getSaleStartDate(), ticketCategory.getSaleEndDate());

        ticketCategory.setEventId(eventId);
        return ticketCategoryDAO.create(ticketCategory);
    }

    public TicketCategory updateTicketCategory(Long id, TicketCategory ticketCategory) {
        TicketCategory existing = ticketCategoryDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TicketCategory", id));

        validateSaleDates(ticketCategory.getSaleStartDate(), ticketCategory.getSaleEndDate());

        ticketCategory.setId(id);
        ticketCategory.setEventId(existing.getEventId());
        return ticketCategoryDAO.update(ticketCategory);
    }

    public void deleteTicketCategory(Long id) {
        ticketCategoryDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TicketCategory", id));
        ticketCategoryDAO.delete(id);
    }

    private void validateSaleDates(LocalDateTime saleStartDate, LocalDateTime saleEndDate) {
        if (saleStartDate != null && saleEndDate != null) {
            if (saleStartDate.isAfter(saleEndDate)) {
                throw new IllegalArgumentException("Sale start date must be before sale end date");
            }
        }
    }
}
