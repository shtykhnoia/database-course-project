package com.example.ticketingsystem.controller;

import com.example.ticketingsystem.dto.mapper.EventMapper;
import com.example.ticketingsystem.dto.request.EventRequest;
import com.example.ticketingsystem.dto.request.StatusUpdateRequest;
import com.example.ticketingsystem.dto.response.EventResponse;
import com.example.ticketingsystem.model.Event;
import com.example.ticketingsystem.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    @GetMapping
    public List<EventResponse> getAllEvents() {
        return eventService.getAllEvents().stream()
                .map(EventResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(event -> ResponseEntity.ok(new EventResponse(event)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        Event event = eventMapper.toEntity(request);
        Event createdEvent = eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EventResponse(createdEvent));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequest request) {
        Event event = eventMapper.toEntity(request);
        Event updatedEvent = eventService.updateEvent(id, event);
        return ResponseEntity.ok(new EventResponse(updatedEvent));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateEventStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        String status = request.getStatus();
        if ("published".equals(status)) {
            eventService.publishEvent(id);
        } else if ("cancelled".equals(status)) {
            eventService.cancelEvent(id);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/published")
    public List<EventResponse> getPublishedEvents() {
        return eventService.getPublishedEvents().stream()
                .map(EventResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/organizer/{organizerId}")
    public List<EventResponse> getEventsByOrganizerId(@PathVariable Long organizerId) {
        return eventService.getEventsByOrganizerId(organizerId).stream()
                .map(EventResponse::new)
                .collect(Collectors.toList());
    }
}
