package com.example.ticketingsystem.service;

import com.example.ticketingsystem.exception.ResourceNotFoundException;
import com.example.ticketingsystem.model.Venue;
import com.example.ticketingsystem.repository.VenueDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VenueService {

    private final VenueDAO venueDAO;

    public VenueService(VenueDAO venueDAO) {
        this.venueDAO = venueDAO;
    }

    @Transactional
    public Venue createVenue(Venue venue) {
        return venueDAO.create(venue);
    }

    public Venue getVenueById(Long id) {
        return venueDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", id));
    }

    public List<Venue> getAllVenues() {
        return venueDAO.findAll();
    }

    @Transactional
    public Venue updateVenue(Long id, Venue venue) {
        Venue existing = getVenueById(id);
        venue.setId(existing.getId());
        return venueDAO.update(venue);
    }

    @Transactional
    public void deleteVenue(Long id) {
        int deleted = venueDAO.delete(id);
        if (deleted == 0) {
            throw new ResourceNotFoundException("Venue", id);
        }
    }
}
