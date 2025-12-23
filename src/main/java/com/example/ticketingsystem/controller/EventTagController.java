package com.example.ticketingsystem.controller;

import com.example.ticketingsystem.dto.mapper.EventTagMapper;
import com.example.ticketingsystem.dto.request.EventTagRequest;
import com.example.ticketingsystem.dto.response.EventTagResponse;
import com.example.ticketingsystem.model.EventTag;
import com.example.ticketingsystem.service.EventTagService;
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
@RequestMapping("/api/event-tags")
@AllArgsConstructor
@Tag(name = "Теги мероприятий", description = "Управление тегами для категоризации мероприятий")
public class EventTagController {

    private final EventTagService eventTagService;
    private final EventTagMapper eventTagMapper;

    @PostMapping
    @Operation(summary = "Создать тег",
               description = "Создает новый тег для мероприятий (требуется роль ORGANIZER или ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "Тег создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    public ResponseEntity<EventTagResponse> createEventTag(@Valid @RequestBody EventTagRequest request) {
        EventTag eventTag = eventTagMapper.toEntity(request);
        EventTag created = eventTagService.createEventTag(eventTag);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EventTagResponse(created));
    }

    @GetMapping
    @Operation(summary = "Получить все теги",
               description = "Возвращает список всех доступных тегов (публичный доступ)")
    @ApiResponse(responseCode = "200", description = "Список тегов")
    public ResponseEntity<List<EventTagResponse>> getAllEventTags() {
        List<EventTag> tags = eventTagService.getAllEventTags();
        List<EventTagResponse> responses = tags.stream()
                .map(EventTagResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить тег по ID",
               description = "Возвращает информацию о теге (публичный доступ)")
    @ApiResponse(responseCode = "200", description = "Тег найден")
    @ApiResponse(responseCode = "404", description = "Тег не найден")
    public ResponseEntity<EventTagResponse> getEventTagById(
            @Parameter(description = "ID тега") @PathVariable Long id) {
        EventTag eventTag = eventTagService.getEventTagById(id);
        return ResponseEntity.ok(new EventTagResponse(eventTag));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить тег",
               description = "Удаляет тег (требуется роль ORGANIZER или ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Тег удален")
    @ApiResponse(responseCode = "404", description = "Тег не найден")
    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    public ResponseEntity<Void> deleteEventTag(
            @Parameter(description = "ID тега") @PathVariable Long id) {
        eventTagService.deleteEventTag(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/events/{eventId}/tags/{tagId}")
    @Operation(summary = "Назначить тег мероприятию",
               description = "Связывает тег с мероприятием (требуется роль ORGANIZER или ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Тег назначен")
    @ApiResponse(responseCode = "404", description = "Мероприятие или тег не найдены")
    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    public ResponseEntity<Void> assignTagToEvent(
            @Parameter(description = "ID мероприятия") @PathVariable Long eventId,
            @Parameter(description = "ID тега") @PathVariable Long tagId) {
        eventTagService.assignTagToEvent(eventId, tagId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/events/{eventId}/tags/{tagId}")
    @Operation(summary = "Удалить тег у мероприятия",
               description = "Удаляет связь тега с мероприятием (требуется роль ORGANIZER или ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Тег удален у мероприятия")
    @ApiResponse(responseCode = "404", description = "Связь не найдена")
    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    public ResponseEntity<Void> removeTagFromEvent(
            @Parameter(description = "ID мероприятия") @PathVariable Long eventId,
            @Parameter(description = "ID тега") @PathVariable Long tagId) {
        eventTagService.removeTagFromEvent(eventId, tagId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/events/{eventId}")
    @Operation(summary = "Получить теги мероприятия",
               description = "Возвращает все теги конкретного мероприятия (публичный доступ)")
    @ApiResponse(responseCode = "200", description = "Список тегов мероприятия")
    public ResponseEntity<List<EventTagResponse>> getEventTags(
            @Parameter(description = "ID мероприятия") @PathVariable Long eventId) {
        List<EventTag> tags = eventTagService.getEventTagsByEventId(eventId);
        List<EventTagResponse> responses = tags.stream()
                .map(EventTagResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
