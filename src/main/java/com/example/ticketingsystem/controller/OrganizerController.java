package com.example.ticketingsystem.controller;

import com.example.ticketingsystem.dto.mapper.OrganizerMapper;
import com.example.ticketingsystem.dto.request.OrganizerRequest;
import com.example.ticketingsystem.dto.response.OrganizerResponse;
import com.example.ticketingsystem.model.Organizer;
import com.example.ticketingsystem.service.OrganizerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizers")
@AllArgsConstructor
public class OrganizerController {

    private final OrganizerService organizerService;
    private final OrganizerMapper organizerMapper;

    @GetMapping
    public ResponseEntity<List<OrganizerResponse>> getAllOrganizers() {
        List<Organizer> organizers = organizerService.getAllOrganizers();
        List<OrganizerResponse> responses = organizers.stream()
                .map(OrganizerResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizerResponse> getOrganizerById(@PathVariable Long id) {
        Organizer organizer = organizerService.getOrganizerById(id);
        return ResponseEntity.ok(new OrganizerResponse(organizer));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<OrganizerResponse> getOrganizerByUserId(@PathVariable Long userId) {
        Organizer organizer = organizerService.getOrganizerByUserId(userId);
        return ResponseEntity.ok(new OrganizerResponse(organizer));
    }

    @PostMapping
    public ResponseEntity<OrganizerResponse> createOrganizer(@Valid @RequestBody OrganizerRequest request) {
        Organizer organizer = organizerMapper.toEntity(request);
        Organizer created = organizerService.createOrganizer(organizer);
        return ResponseEntity.status(HttpStatus.CREATED).body(new OrganizerResponse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizerResponse> updateOrganizer(
            @PathVariable Long id,
            @Valid @RequestBody OrganizerRequest request) {
        Organizer organizer = organizerMapper.toEntity(request);
        Organizer updated = organizerService.updateOrganizer(id, organizer);
        return ResponseEntity.ok(new OrganizerResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganizer(@PathVariable Long id) {
        organizerService.deleteOrganizer(id);
        return ResponseEntity.noContent().build();
    }
}
