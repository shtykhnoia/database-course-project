package com.example.ticketingsystem.dto.mapper;

import com.example.ticketingsystem.dto.request.EventTagRequest;
import com.example.ticketingsystem.model.EventTag;
import org.springframework.stereotype.Component;

@Component
public class EventTagMapper {

    public EventTag toEntity(EventTagRequest request) {
        EventTag eventTag = new EventTag();
        eventTag.setName(request.getName());
        return eventTag;
    }
}
