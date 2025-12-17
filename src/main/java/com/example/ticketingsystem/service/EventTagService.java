package com.example.ticketingsystem.service;

import com.example.ticketingsystem.exception.DuplicateResourceException;
import com.example.ticketingsystem.exception.ResourceNotFoundException;
import com.example.ticketingsystem.model.EventTag;
import com.example.ticketingsystem.repository.EventTagDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventTagService {

    private final EventTagDAO eventTagDAO;

    public EventTagService(EventTagDAO eventTagDAO) {
        this.eventTagDAO = eventTagDAO;
    }

    @Transactional
    public EventTag createEventTag(EventTag eventTag) {
        // Проверка на дубликат по имени
        eventTagDAO.findByName(eventTag.getName()).ifPresent(existing -> {
            throw new DuplicateResourceException("Event tag with name '" + eventTag.getName() + "' already exists");
        });
        return eventTagDAO.create(eventTag);
    }

    public EventTag getEventTagById(Long id) {
        return eventTagDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event tag", id));
    }

    public List<EventTag> getAllEventTags() {
        return eventTagDAO.findAll();
    }

    public List<EventTag> getEventTagsByEventId(Long eventId) {
        return eventTagDAO.findByEventId(eventId);
    }

    @Transactional
    public void deleteEventTag(Long id) {
        int deleted = eventTagDAO.delete(id);
        if (deleted == 0) {
            throw new ResourceNotFoundException("Event tag", id);
        }
    }

    @Transactional
    public void assignTagToEvent(Long eventId, Long tagId) {
        // Проверяем что тег существует
        getEventTagById(tagId);
        eventTagDAO.assignTagToEvent(eventId, tagId);
    }

    @Transactional
    public void removeTagFromEvent(Long eventId, Long tagId) {
        int removed = eventTagDAO.removeTagFromEvent(eventId, tagId);
        if (removed == 0) {
            throw new ResourceNotFoundException("Tag assignment not found");
        }
    }
}
