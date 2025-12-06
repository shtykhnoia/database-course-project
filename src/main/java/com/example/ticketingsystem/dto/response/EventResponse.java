package com.example.ticketingsystem.dto.response;

import com.example.ticketingsystem.model.Event;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private Long organizerId;
    private LocalDateTime startDatetime;
    private String eventStatus;

    public EventResponse(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.organizerId = event.getOrganizerId();
        this.startDatetime = event.getStartDatetime();
        this.eventStatus = event.getEventStatus();
    }
}
