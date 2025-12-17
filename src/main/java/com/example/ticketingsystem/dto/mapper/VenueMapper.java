package com.example.ticketingsystem.dto.mapper;

import com.example.ticketingsystem.dto.request.VenueRequest;
import com.example.ticketingsystem.model.Venue;
import org.springframework.stereotype.Component;

@Component
public class VenueMapper {

    public Venue toEntity(VenueRequest request) {
        Venue venue = new Venue();
        venue.setName(request.getName());
        venue.setAddress(request.getAddress());
        venue.setCapacity(request.getCapacity());
        return venue;
    }

    public void updateEntity(Venue venue, VenueRequest request) {
        venue.setName(request.getName());
        venue.setAddress(request.getAddress());
        venue.setCapacity(request.getCapacity());
    }
}
