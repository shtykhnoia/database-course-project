package com.example.ticketingsystem.service;

import com.example.ticketingsystem.exception.ResourceNotFoundException;
import com.example.ticketingsystem.model.Event;
import com.example.ticketingsystem.model.Order;
import com.example.ticketingsystem.model.Ticket;
import com.example.ticketingsystem.repository.EventDAO;
import com.example.ticketingsystem.repository.OrderDAO;
import com.example.ticketingsystem.repository.TicketDAO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventDAO eventDAO;
    private final TicketDAO ticketDAO;
    private final OrderDAO orderDAO;
    private final OrderService orderService;

    public EventService(EventDAO eventDAO, TicketDAO ticketDAO, OrderDAO orderDAO, @Lazy OrderService orderService) {
        this.eventDAO = eventDAO;
        this.ticketDAO = ticketDAO;
        this.orderDAO = orderDAO;
        this.orderService = orderService;
    }

    public List<Event> getAllEvents() {
        return eventDAO.getAllEvents();
    }

    public Optional<Event> getEventById(Long id) {
        return eventDAO.getEventById(id);
    }

    public List<Event> getEventsByOrganizerId(Long organizerId) {
        return eventDAO.getEventsByOrganizerId(organizerId);
    }

    public List<Event> getPublishedEvents() {
        return eventDAO.getEventsByStatus("published");
    }

    public Event createEvent(Event event) {
        if (event.getEventStatus() == null || event.getEventStatus().isBlank()) {
            event.setEventStatus("draft");
        }

        return eventDAO.createEvent(event);
    }

    public Event updateEvent(Long id, Event event) {
        Event existing = eventDAO.getEventById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));

        int confirmedOrders = eventDAO.countConfirmedOrdersByEventId(id);

        if (confirmedOrders > 0) {
            if (!existing.getStartDatetime().equals(event.getStartDatetime()) ||
                    !existing.getVenueId().equals(event.getVenueId())) {
                throw new IllegalStateException(
                        "Cannot change date/venue for event with sold tickets. Sold tickets: " + confirmedOrders
                );
            }
        }

        event.setId(id);
        return eventDAO.updateEvent(event);
    }

    public void publishEvent(Long eventId) {
        eventDAO.updateEventStatus(eventId, "published");
    }

    @Transactional
    public void cancelEvent(Long eventId) {
        eventDAO.getEventById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));

        int confirmedOrders = eventDAO.countConfirmedOrdersByEventId(eventId);
        if (confirmedOrders > 0) {
            throw new IllegalStateException(
                    "Cannot cancel event with confirmed orders. Please process refunds first. Affected orders: " + confirmedOrders
            );
        }

        List<Order> pendingOrders = orderDAO.findByEventIdAndStatus(eventId, "pending");
        for (Order order : pendingOrders) {
            orderService.cancelOrder(order.getId());
        }

        eventDAO.updateEventStatus(eventId, "cancelled");
    }

    @Transactional
    public void deleteEvent(Long id) {
        eventDAO.getEventById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));

        int orderCount = eventDAO.countOrdersByEventId(id);
        if (orderCount > 0) {
            throw new IllegalStateException("Cannot delete event with existing orders");
        }

        List<Ticket> tickets = ticketDAO.findByEventId(id);
        if (!tickets.isEmpty()) {
            List<Long> ticketIds = tickets.stream()
                    .map(Ticket::getId)
                    .toList();
            ticketDAO.batchUpdateStatus(ticketIds, "cancelled");
        }

        eventDAO.deleteEvent(id);
    }
}