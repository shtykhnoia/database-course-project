package com.example.ticketingsystem.controller;

import com.example.ticketingsystem.dto.mapper.TicketCategoryMapper;
import com.example.ticketingsystem.dto.request.TicketCategoryRequest;
import com.example.ticketingsystem.dto.response.TicketCategoryResponse;
import com.example.ticketingsystem.model.TicketCategory;
import com.example.ticketingsystem.service.TicketCategoryService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Tag(name = "Категории билетов", description = "Управление категориями билетов для мероприятий")
@AllArgsConstructor
public class TicketCategoryController {

    private final TicketCategoryService ticketCategoryService;
    private final TicketCategoryMapper ticketCategoryMapper;

    @GetMapping("/events/{eventId}/tickets")
    @Operation(summary = "Получить категории билетов мероприятия",
               description = "Возвращает все доступные категории билетов для конкретного мероприятия (публичный доступ)")
    @ApiResponse(responseCode = "200", description = "Список категорий билетов")
    public List<TicketCategoryResponse> getTicketCategoriesByEvent(
            @Parameter(description = "ID мероприятия") @PathVariable Long eventId) {
        return ticketCategoryService.getTicketCategoriesByEventId(eventId).stream()
                .map(TicketCategoryResponse::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/events/{eventId}/tickets")
    @Operation(summary = "Создать категорию билетов",
               description = "Создает новую категорию билетов для мероприятия (требуется роль ORGANIZER или ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "Категория создана")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    public ResponseEntity<TicketCategoryResponse> createTicketCategory(
            @Parameter(description = "ID мероприятия") @PathVariable Long eventId,
            @Valid @RequestBody TicketCategoryRequest request) {

        TicketCategory ticketCategory = ticketCategoryMapper.toEntity(request, eventId);
        TicketCategory created = ticketCategoryService.createTicketCategory(eventId, ticketCategory);

        return ResponseEntity.status(HttpStatus.CREATED).body(new TicketCategoryResponse(created));
    }

    @GetMapping("/tickets/{id}")
    @Operation(summary = "Получить категорию билетов по ID",
               description = "Возвращает детальную информацию о категории билетов (публичный доступ)")
    @ApiResponse(responseCode = "200", description = "Категория найдена")
    @ApiResponse(responseCode = "404", description = "Категория не найдена")
    public ResponseEntity<TicketCategoryResponse> getTicketCategoryById(
            @Parameter(description = "ID категории билетов") @PathVariable Long id) {
        return ticketCategoryService.getTicketCategoryById(id)
                .map(tc -> ResponseEntity.ok(new TicketCategoryResponse(tc)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/tickets/{id}")
    @Operation(summary = "Обновить категорию билетов",
               description = "Обновляет категорию билетов (цена, количество, даты продаж). Требуется роль ORGANIZER или ADMIN")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Категория обновлена")
    @ApiResponse(responseCode = "404", description = "Категория не найдена")
    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    public ResponseEntity<TicketCategoryResponse> updateTicketCategory(
            @Parameter(description = "ID категории билетов") @PathVariable Long id,
            @Valid @RequestBody TicketCategoryRequest request) {

        TicketCategory existing = ticketCategoryService.getTicketCategoryById(id)
                .orElseThrow(() -> new RuntimeException("TicketCategory not found"));

        ticketCategoryMapper.updateEntity(existing, request);
        TicketCategory updated = ticketCategoryService.updateTicketCategory(id, existing);

        return ResponseEntity.ok(new TicketCategoryResponse(updated));
    }

    @DeleteMapping("/tickets/{id}")
    @Operation(summary = "Удалить категорию билетов",
               description = "Удаляет категорию билетов (требуется роль ORGANIZER или ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Категория удалена")
    @ApiResponse(responseCode = "404", description = "Категория не найдена")
    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    public ResponseEntity<Void> deleteTicketCategory(
            @Parameter(description = "ID категории билетов") @PathVariable Long id) {
        ticketCategoryService.deleteTicketCategory(id);
        return ResponseEntity.noContent().build();
    }
}
