package com.example.ticketingsystem.controller;

import com.example.ticketingsystem.dto.mapper.OrganizerMapper;
import com.example.ticketingsystem.dto.request.OrganizerRequest;
import com.example.ticketingsystem.dto.response.OrganizerResponse;
import com.example.ticketingsystem.model.Organizer;
import com.example.ticketingsystem.service.OrganizerService;
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
@RequestMapping("/api/organizers")
@AllArgsConstructor
@Tag(name = "Организаторы", description = "Управление организаторами мероприятий")
public class OrganizerController {

    private final OrganizerService organizerService;
    private final OrganizerMapper organizerMapper;

    @GetMapping
    @Operation(summary = "Получить всех организаторов", description = "Возвращает список всех организаторов (публичный доступ)")
    @ApiResponse(responseCode = "200", description = "Список организаторов")
    public ResponseEntity<List<OrganizerResponse>> getAllOrganizers() {
        List<Organizer> organizers = organizerService.getAllOrganizers();
        List<OrganizerResponse> responses = organizers.stream()
                .map(OrganizerResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить организатора по ID", description = "Возвращает информацию об организаторе (публичный доступ)")
    @ApiResponse(responseCode = "200", description = "Организатор найден")
    @ApiResponse(responseCode = "404", description = "Организатор не найден")
    public ResponseEntity<OrganizerResponse> getOrganizerById(@Parameter(description = "ID организатора") @PathVariable Long id) {
        Organizer organizer = organizerService.getOrganizerById(id);
        return ResponseEntity.ok(new OrganizerResponse(organizer));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Получить организатора по ID пользователя", description = "Находит организатора, связанного с конкретным пользователем")
    @ApiResponse(responseCode = "200", description = "Организатор найден")
    @ApiResponse(responseCode = "404", description = "Организатор не найден")
    public ResponseEntity<OrganizerResponse> getOrganizerByUserId(@Parameter(description = "ID пользователя") @PathVariable Long userId) {
        Organizer organizer = organizerService.getOrganizerByUserId(userId);
        return ResponseEntity.ok(new OrganizerResponse(organizer));
    }

    @PostMapping
    @Operation(summary = "Создать организатора", description = "Создает нового организатора (требуется роль ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "Организатор создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    @ApiResponse(responseCode = "403", description = "Требуется роль ADMIN")
    public ResponseEntity<OrganizerResponse> createOrganizer(@Valid @RequestBody OrganizerRequest request) {
        Organizer organizer = organizerMapper.toEntity(request);
        Organizer created = organizerService.createOrganizer(organizer);
        return ResponseEntity.status(HttpStatus.CREATED).body(new OrganizerResponse(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить организатора", description = "Обновляет данные организатора (требуется роль ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Организатор обновлен")
    @ApiResponse(responseCode = "404", description = "Организатор не найден")
    @ApiResponse(responseCode = "403", description = "Требуется роль ADMIN")
    public ResponseEntity<OrganizerResponse> updateOrganizer(
            @Parameter(description = "ID организатора") @PathVariable Long id,
            @Valid @RequestBody OrganizerRequest request) {
        Organizer organizer = organizerMapper.toEntity(request);
        Organizer updated = organizerService.updateOrganizer(id, organizer);
        return ResponseEntity.ok(new OrganizerResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить организатора", description = "Удаляет организатора (требуется роль ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Организатор удален")
    @ApiResponse(responseCode = "404", description = "Организатор не найден")
    @ApiResponse(responseCode = "403", description = "Требуется роль ADMIN")
    public ResponseEntity<Void> deleteOrganizer(@Parameter(description = "ID организатора") @PathVariable Long id) {
        organizerService.deleteOrganizer(id);
        return ResponseEntity.noContent().build();
    }
}
