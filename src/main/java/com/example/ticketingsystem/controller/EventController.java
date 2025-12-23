package com.example.ticketingsystem.controller;

import com.example.ticketingsystem.dto.mapper.EventMapper;
import com.example.ticketingsystem.dto.request.EventRequest;
import com.example.ticketingsystem.dto.request.StatusUpdateRequest;
import com.example.ticketingsystem.dto.response.EventResponse;
import com.example.ticketingsystem.model.Event;
import com.example.ticketingsystem.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Мероприятия", description = "Управление мероприятиями и категориями билетов")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    @GetMapping
    @Operation(summary = "Получить все мероприятия", description = "Возвращает список всех мероприятий (публичный доступ)")
    public List<EventResponse> getAllEvents() {
        return eventService.getAllEvents().stream()
                .map(EventResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить мероприятие по ID", description = "Возвращает детальную информацию о мероприятии (публичный доступ)")
    public ResponseEntity<EventResponse> getEventById(@Parameter(description = "ID мероприятия") @PathVariable Long id) {
        return eventService.getEventById(id)
                .map(event -> ResponseEntity.ok(new EventResponse(event)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Создать мероприятие",
               description = "Создает новое мероприятие (требуется роль ORGANIZER или ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "Мероприятие создано")
    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        Event event = eventMapper.toEntity(request);
        Event createdEvent = eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EventResponse(createdEvent));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить мероприятие",
               description = "Обновляет существующее мероприятие (требуется роль ORGANIZER или ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Мероприятие обновлено")
    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    @ApiResponse(responseCode = "404", description = "Мероприятие не найдено")
    public ResponseEntity<EventResponse> updateEvent(@Parameter(description = "ID мероприятия") @PathVariable Long id, @Valid @RequestBody EventRequest request) {
        Event event = eventMapper.toEntity(request);
        Event updatedEvent = eventService.updateEvent(id, event);
        return ResponseEntity.ok(new EventResponse(updatedEvent));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить мероприятие",
               description = "Удаляет мероприятие (требуется роль ORGANIZER или ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Мероприятие удалено")
    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    public ResponseEntity<Void> deleteEvent(@Parameter(description = "ID мероприятия") @PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Изменить статус мероприятия",
               description = "Публикует или отменяет мероприятие (требуется роль ORGANIZER или ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Статус изменен")
    @ApiResponse(responseCode = "400", description = "Некорректный статус")
    @ApiResponse(responseCode = "404", description = "Мероприятие не найдено")
    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    public ResponseEntity<Void> updateEventStatus(@Parameter(description = "ID мероприятия") @PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        String status = request.getStatus();
        if ("published".equals(status)) {
            eventService.publishEvent(id);
        } else if ("cancelled".equals(status)) {
            eventService.cancelEvent(id);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/published")
    @Operation(summary = "Получить опубликованные мероприятия",
               description = "Возвращает только мероприятия в статусе 'published' (публичный доступ)")
    @ApiResponse(responseCode = "200", description = "Список опубликованных мероприятий")
    public List<EventResponse> getPublishedEvents() {
        return eventService.getPublishedEvents().stream()
                .map(EventResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/organizer/{organizerId}")
    @Operation(summary = "Получить мероприятия организатора",
               description = "Возвращает все мероприятия конкретного организатора (публичный доступ)")
    @ApiResponse(responseCode = "200", description = "Список мероприятий организатора")
    public List<EventResponse> getEventsByOrganizerId(@Parameter(description = "ID организатора") @PathVariable Long organizerId) {
        return eventService.getEventsByOrganizerId(organizerId).stream()
                .map(EventResponse::new)
                .collect(Collectors.toList());
    }
}
