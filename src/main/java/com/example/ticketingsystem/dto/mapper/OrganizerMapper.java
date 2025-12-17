package com.example.ticketingsystem.dto.mapper;

import com.example.ticketingsystem.dto.request.OrganizerRequest;
import com.example.ticketingsystem.model.Organizer;
import org.springframework.stereotype.Component;

@Component
public class OrganizerMapper {

    public Organizer toEntity(OrganizerRequest request) {
        Organizer organizer = new Organizer();
        organizer.setName(request.getName());
        organizer.setDescription(request.getDescription());
        organizer.setContactEmail(request.getContactEmail());
        organizer.setContactPhone(request.getContactPhone());
        organizer.setUserId(request.getUserId());
        return organizer;
    }
}
