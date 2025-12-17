package com.example.ticketingsystem.dto.mapper;

import com.example.ticketingsystem.dto.request.EventRequest;
import com.example.ticketingsystem.model.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public Event toEntity(EventRequest request) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setOrganizerId(request.getOrganizerId());
        event.setVenueId(request.getVenueId());
        event.setStartDatetime(request.getStartDatetime());
        event.setEndDatetime(request.getEndDatetime());
        event.setEventStatus(request.getEventStatus());
        return event;
    }

    public void updateEntity(Event event, EventRequest request) {
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setOrganizerId(request.getOrganizerId());
        event.setVenueId(request.getVenueId());
        event.setStartDatetime(request.getStartDatetime());
        event.setEndDatetime(request.getEndDatetime());
        event.setEventStatus(request.getEventStatus());
    }
}
