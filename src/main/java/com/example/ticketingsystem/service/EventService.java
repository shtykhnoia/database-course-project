package com.example.ticketingsystem.service;

import com.example.ticketingsystem.exception.ResourceNotFoundException;
import com.example.ticketingsystem.model.Event;
import com.example.ticketingsystem.repository.EventDAO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventDAO eventDAO;

    public EventService(EventDAO eventDAO) {
        this.eventDAO = eventDAO;
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
        eventDAO.getEventById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));

        event.setId(id);
        return eventDAO.updateEvent(event);
    }

    public void publishEvent(Long eventId) {
        eventDAO.updateEventStatus(eventId, "published");
    }

    public void cancelEvent(Long eventId) {
        eventDAO.updateEventStatus(eventId, "cancelled");
    }

    public void deleteEvent(Long id) {
        eventDAO.deleteEvent(id);
    }
}