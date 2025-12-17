package com.example.ticketingsystem.service;

import com.example.ticketingsystem.exception.ResourceNotFoundException;
import com.example.ticketingsystem.model.Organizer;
import com.example.ticketingsystem.repository.OrganizerDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrganizerService {

    private final OrganizerDAO organizerDAO;

    public List<Organizer> getAllOrganizers() {
        return organizerDAO.findAll();
    }

    public Organizer getOrganizerById(Long id) {
        return organizerDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer", id));
    }

    public Organizer getOrganizerByUserId(Long userId) {
        return organizerDAO.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer for user", userId));
    }

    public Organizer createOrganizer(Organizer organizer) {
        return organizerDAO.create(organizer);
    }

    public Organizer updateOrganizer(Long id, Organizer organizer) {
        organizerDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer", id));

        organizer.setId(id);
        return organizerDAO.update(organizer);
    }

    public void deleteOrganizer(Long id) {
        organizerDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer", id));
        organizerDAO.delete(id);
    }
}
