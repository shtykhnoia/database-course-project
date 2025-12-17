package com.example.ticketingsystem.dto.response;

import com.example.ticketingsystem.model.Venue;
import lombok.Data;

@Data
public class VenueResponse {
    private Long id;
    private String name;
    private String address;
    private Integer capacity;

    public VenueResponse(Venue venue) {
        this.id = venue.getId();
        this.name = venue.getName();
        this.address = venue.getAddress();
        this.capacity = venue.getCapacity();
    }
}
