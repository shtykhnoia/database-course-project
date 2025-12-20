package com.example.ticketingsystem.service;

import com.example.ticketingsystem.exception.ResourceNotFoundException;
import com.example.ticketingsystem.model.Event;
import com.example.ticketingsystem.model.TicketCategory;
import com.example.ticketingsystem.repository.EventDAO;
import com.example.ticketingsystem.repository.OrderItemDAO;
import com.example.ticketingsystem.repository.TicketCategoryDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketCategoryService {

    private final TicketCategoryDAO ticketCategoryDAO;
    private final EventDAO eventDAO;
    private final OrderItemDAO orderItemDAO;

    public TicketCategoryService(TicketCategoryDAO ticketCategoryDAO, EventDAO eventDAO, OrderItemDAO orderItemDAO) {
        this.ticketCategoryDAO = ticketCategoryDAO;
        this.eventDAO = eventDAO;
        this.orderItemDAO = orderItemDAO;
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

    @Transactional
    public void deleteTicketCategory(Long id) {
        ticketCategoryDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TicketCategory", id));

        int orderCount = orderItemDAO.countByTicketCategoryId(id);
        if (orderCount > 0) {
            throw new IllegalStateException("Cannot delete ticket category with existing orders");
        }

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
