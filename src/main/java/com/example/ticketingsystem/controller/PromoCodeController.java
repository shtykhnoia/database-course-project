package com.example.ticketingsystem.controller;

import com.example.ticketingsystem.dto.mapper.PromoCodeMapper;
import com.example.ticketingsystem.dto.request.PromoCodeRequest;
import com.example.ticketingsystem.dto.response.PromoCodeResponse;
import com.example.ticketingsystem.model.PromoCode;
import com.example.ticketingsystem.service.PromoCodeService;
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
@RequestMapping("/api/promo-codes")
@AllArgsConstructor
@Tag(name = "Промокоды", description = "Управление промокодами для скидок на билеты")
@SecurityRequirement(name = "bearerAuth")
public class PromoCodeController {
    private final PromoCodeService promoCodeService;
    private final PromoCodeMapper mapper;

    @PostMapping
    @Operation(summary = "Создать промокод",
               description = "Создает новый промокод с указанной скидкой и сроком действия (требуется роль ORGANIZER или ADMIN)")
    @ApiResponse(responseCode = "201", description = "Промокод создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    public ResponseEntity<PromoCodeResponse> createPromoCode(@Valid @RequestBody PromoCodeRequest promo) {
        PromoCode promoCode = mapper.toEntity(promo);
        PromoCode created = promoCodeService.createPromocode(promoCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PromoCodeResponse(created));
    }

    @GetMapping
    @Operation(summary = "Получить все промокоды",
               description = "Возвращает список всех промокодов, можно фильтровать по мероприятию")
    @ApiResponse(responseCode = "200", description = "Список промокодов")
    public ResponseEntity<List<PromoCodeResponse>> getAllPromoCodes(
            @Parameter(description = "ID мероприятия (опционально)") @RequestParam(required = false) Long eventId) {
        List<PromoCode> promoCodes;

        if (eventId != null) {
            promoCodes = promoCodeService.getPromoCodesByEventId(eventId);
        } else {
            promoCodes = promoCodeService.getAllPromoCodes();
        }

        List<PromoCodeResponse> responses = promoCodes.stream()
                .map(PromoCodeResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить промокод по ID",
               description = "Возвращает детальную информацию о промокоде")
    @ApiResponse(responseCode = "200", description = "Промокод найден")
    @ApiResponse(responseCode = "404", description = "Промокод не найден")
    public ResponseEntity<PromoCodeResponse> getPromoCodeById(
            @Parameter(description = "ID промокода") @PathVariable Long id) {
        PromoCode promoCode = promoCodeService.getPromoCodeById(id);
        return ResponseEntity.ok(new PromoCodeResponse(promoCode));
    }

    @GetMapping("/by-code/{code}")
    @Operation(summary = "Получить промокод по коду",
               description = "Находит промокод по его текстовому коду (например, 'SALE2025')")
    @ApiResponse(responseCode = "200", description = "Промокод найден")
    @ApiResponse(responseCode = "404", description = "Промокод не найден")
    public ResponseEntity<PromoCodeResponse> getPromoCodeByCode(
            @Parameter(description = "Код промокода") @PathVariable String code) {
        PromoCode promoCode = promoCodeService.getPromoCodeByCode(code);
        return ResponseEntity.ok(new PromoCodeResponse(promoCode));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить промокод",
               description = "Обновляет параметры промокода (скидка, срок действия, лимиты). Требуется роль ORGANIZER или ADMIN")
    @ApiResponse(responseCode = "200", description = "Промокод обновлен")
    @ApiResponse(responseCode = "404", description = "Промокод не найден")
    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    public ResponseEntity<PromoCodeResponse> updatePromoCode(
            @Parameter(description = "ID промокода") @PathVariable Long id,
            @Valid @RequestBody PromoCodeRequest request) {
        PromoCode promoCode = mapper.toEntity(request);
        PromoCode updated = promoCodeService.updatePromoCode(id, promoCode);
        return ResponseEntity.ok(new PromoCodeResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить промокод",
               description = "Удаляет промокод (требуется роль ORGANIZER или ADMIN)")
    @ApiResponse(responseCode = "204", description = "Промокод удален")
    @ApiResponse(responseCode = "404", description = "Промокод не найден")
    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    public ResponseEntity<Void> deletePromoCode(
            @Parameter(description = "ID промокода") @PathVariable Long id) {
        promoCodeService.deletePromoCode(id);
        return ResponseEntity.noContent().build();
    }

}
