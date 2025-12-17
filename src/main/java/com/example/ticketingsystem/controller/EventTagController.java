package com.example.ticketingsystem.controller;

import com.example.ticketingsystem.dto.mapper.EventTagMapper;
import com.example.ticketingsystem.dto.request.EventTagRequest;
import com.example.ticketingsystem.dto.response.EventTagResponse;
import com.example.ticketingsystem.model.EventTag;
import com.example.ticketingsystem.service.EventTagService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event-tags")
@AllArgsConstructor
public class EventTagController {

    private final EventTagService eventTagService;
    private final EventTagMapper eventTagMapper;

    @PostMapping
    public ResponseEntity<EventTagResponse> createEventTag(@Valid @RequestBody EventTagRequest request) {
        EventTag eventTag = eventTagMapper.toEntity(request);
        EventTag created = eventTagService.createEventTag(eventTag);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EventTagResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<EventTagResponse>> getAllEventTags() {
        List<EventTag> tags = eventTagService.getAllEventTags();
        List<EventTagResponse> responses = tags.stream()
                .map(EventTagResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventTagResponse> getEventTagById(@PathVariable Long id) {
        EventTag eventTag = eventTagService.getEventTagById(id);
        return ResponseEntity.ok(new EventTagResponse(eventTag));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventTag(@PathVariable Long id) {
        eventTagService.deleteEventTag(id);
        return ResponseEntity.noContent().build();
    }

    // Управление связями Event <-> Tag
    @PostMapping("/events/{eventId}/tags/{tagId}")
    public ResponseEntity<Void> assignTagToEvent(
            @PathVariable Long eventId,
            @PathVariable Long tagId) {
        eventTagService.assignTagToEvent(eventId, tagId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/events/{eventId}/tags/{tagId}")
    public ResponseEntity<Void> removeTagFromEvent(
            @PathVariable Long eventId,
            @PathVariable Long tagId) {
        eventTagService.removeTagFromEvent(eventId, tagId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<List<EventTagResponse>> getEventTags(@PathVariable Long eventId) {
        List<EventTag> tags = eventTagService.getEventTagsByEventId(eventId);
        List<EventTagResponse> responses = tags.stream()
                .map(EventTagResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
