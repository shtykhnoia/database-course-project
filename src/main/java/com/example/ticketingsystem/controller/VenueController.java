package com.example.ticketingsystem.controller;

import com.example.ticketingsystem.dto.mapper.VenueMapper;
import com.example.ticketingsystem.dto.request.VenueRequest;
import com.example.ticketingsystem.dto.response.VenueResponse;
import com.example.ticketingsystem.model.Venue;
import com.example.ticketingsystem.service.VenueService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
@AllArgsConstructor
public class VenueController {

    private final VenueService venueService;
    private final VenueMapper venueMapper;

    @PostMapping
    public ResponseEntity<VenueResponse> createVenue(@Valid @RequestBody VenueRequest request) {
        Venue venue = venueMapper.toEntity(request);
        Venue created = venueService.createVenue(venue);
        return ResponseEntity.status(HttpStatus.CREATED).body(new VenueResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<VenueResponse>> getAllVenues() {
        List<Venue> venues = venueService.getAllVenues();
        List<VenueResponse> responses = venues.stream()
                .map(VenueResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueResponse> getVenueById(@PathVariable Long id) {
        Venue venue = venueService.getVenueById(id);
        return ResponseEntity.ok(new VenueResponse(venue));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VenueResponse> updateVenue(
            @PathVariable Long id,
            @Valid @RequestBody VenueRequest request) {
        Venue venue = venueMapper.toEntity(request);
        Venue updated = venueService.updateVenue(id, venue);
        return ResponseEntity.ok(new VenueResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }
}
