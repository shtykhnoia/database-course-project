package com.example.ticketingsystem.controller;

import com.example.ticketingsystem.dto.mapper.VenueMapper;
import com.example.ticketingsystem.dto.request.VenueRequest;
import com.example.ticketingsystem.dto.response.VenueResponse;
import com.example.ticketingsystem.model.Venue;
import com.example.ticketingsystem.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
@AllArgsConstructor
@Tag(name = "Площадки", description = "Управление площадками проведения мероприятий")
public class VenueController {

    private final VenueService venueService;
    private final VenueMapper venueMapper;

    @GetMapping
    @Operation(summary = "Получить все площадки", description = "Возвращает список всех площадок (публичный доступ)")
    @ApiResponse(responseCode = "200", description = "Список площадок")
    public ResponseEntity<List<VenueResponse>> getAllVenues() {
        List<Venue> venues = venueService.getAllVenues();
        List<VenueResponse> responses = venues.stream()
                .map(VenueResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить площадку по ID", description = "Возвращает детальную информацию о площадке (публичный доступ)")
    @ApiResponse(responseCode = "200", description = "Площадка найдена")
    @ApiResponse(responseCode = "404", description = "Площадка не найдена")
    public ResponseEntity<VenueResponse> getVenueById(@Parameter(description = "ID площадки") @PathVariable Long id) {
        Venue venue = venueService.getVenueById(id);
        return ResponseEntity.ok(new VenueResponse(venue));
    }

    @PostMapping
    @Operation(summary = "Создать площадку", description = "Создает новую площадку (требуется роль ORGANIZER или ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "Площадка создана")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    public ResponseEntity<VenueResponse> createVenue(@Valid @RequestBody VenueRequest request) {
        Venue venue = venueMapper.toEntity(request);
        Venue created = venueService.createVenue(venue);
        return ResponseEntity.status(HttpStatus.CREATED).body(new VenueResponse(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить площадку", description = "Обновляет данные площадки (требуется роль ORGANIZER или ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Площадка обновлена")
    @ApiResponse(responseCode = "404", description = "Площадка не найдена")
    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    public ResponseEntity<VenueResponse> updateVenue(
            @Parameter(description = "ID площадки") @PathVariable Long id,
            @Valid @RequestBody VenueRequest request) {
        Venue venue = venueMapper.toEntity(request);
        Venue updated = venueService.updateVenue(id, venue);
        return ResponseEntity.ok(new VenueResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить площадку", description = "Удаляет площадку (требуется роль ORGANIZER или ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Площадка удалена")
    @ApiResponse(responseCode = "404", description = "Площадка не найдена")
    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    public ResponseEntity<Void> deleteVenue(@Parameter(description = "ID площадки") @PathVariable Long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }
}
