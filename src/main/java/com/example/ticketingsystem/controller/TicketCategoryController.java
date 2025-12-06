package com.example.ticketingsystem.controller;

import com.example.ticketingsystem.dto.mapper.TicketCategoryMapper;
import com.example.ticketingsystem.dto.request.TicketCategoryRequest;
import com.example.ticketingsystem.dto.response.TicketCategoryResponse;
import com.example.ticketingsystem.model.TicketCategory;
import com.example.ticketingsystem.service.TicketCategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TicketCategoryController {

    private final TicketCategoryService ticketCategoryService;
    private final TicketCategoryMapper ticketCategoryMapper;

    public TicketCategoryController(TicketCategoryService ticketCategoryService,
                                   TicketCategoryMapper ticketCategoryMapper) {
        this.ticketCategoryService = ticketCategoryService;
        this.ticketCategoryMapper = ticketCategoryMapper;
    }

    @GetMapping("/events/{eventId}/tickets")
    public List<TicketCategoryResponse> getTicketCategoriesByEvent(@PathVariable Long eventId) {
        return ticketCategoryService.getTicketCategoriesByEventId(eventId).stream()
                .map(TicketCategoryResponse::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/events/{eventId}/tickets")
    public ResponseEntity<TicketCategoryResponse> createTicketCategory(
            @PathVariable Long eventId,
            @Valid @RequestBody TicketCategoryRequest request) {

        TicketCategory ticketCategory = ticketCategoryMapper.toEntity(request, eventId);
        TicketCategory created = ticketCategoryService.createTicketCategory(eventId, ticketCategory);

        return ResponseEntity.status(HttpStatus.CREATED).body(new TicketCategoryResponse(created));
    }

    @GetMapping("/tickets/{id}")
    public ResponseEntity<TicketCategoryResponse> getTicketCategoryById(@PathVariable Long id) {
        return ticketCategoryService.getTicketCategoryById(id)
                .map(tc -> ResponseEntity.ok(new TicketCategoryResponse(tc)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/tickets/{id}")
    public ResponseEntity<TicketCategoryResponse> updateTicketCategory(
            @PathVariable Long id,
            @Valid @RequestBody TicketCategoryRequest request) {

        TicketCategory existing = ticketCategoryService.getTicketCategoryById(id)
                .orElseThrow(() -> new RuntimeException("TicketCategory not found"));

        ticketCategoryMapper.updateEntity(existing, request);
        TicketCategory updated = ticketCategoryService.updateTicketCategory(id, existing);

        return ResponseEntity.ok(new TicketCategoryResponse(updated));
    }

    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<Void> deleteTicketCategory(@PathVariable Long id) {
        ticketCategoryService.deleteTicketCategory(id);
        return ResponseEntity.noContent().build();
    }
}
