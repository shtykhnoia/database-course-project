package com.example.ticketingsystem.dto.response;

import com.example.ticketingsystem.model.EventTag;
import lombok.Data;

@Data
public class EventTagResponse {
    private Long id;
    private String name;

    public EventTagResponse(EventTag eventTag) {
        this.id = eventTag.getId();
        this.name = eventTag.getName();
    }
}
