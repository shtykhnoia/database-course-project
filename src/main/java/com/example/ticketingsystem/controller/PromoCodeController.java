package com.example.ticketingsystem.controller;

import com.example.ticketingsystem.dto.mapper.PromoCodeMapper;
import com.example.ticketingsystem.dto.request.PromoCodeRequest;
import com.example.ticketingsystem.dto.response.PromoCodeResponse;
import com.example.ticketingsystem.model.PromoCode;
import com.example.ticketingsystem.service.PromoCodeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promo-codes")
@AllArgsConstructor
public class PromoCodeController {
    private final PromoCodeService promoCodeService;
    private final PromoCodeMapper mapper;

    @PostMapping
    public ResponseEntity<PromoCodeResponse> createPromoCode(@Valid @RequestBody PromoCodeRequest promo) {
        PromoCode promoCode = mapper.toEntity(promo);
        PromoCode created = promoCodeService.createPromocode(promoCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PromoCodeResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<PromoCodeResponse>> getAllPromoCodes(
            @RequestParam(required = false) Long eventId) {
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
    public ResponseEntity<PromoCodeResponse> getPromoCodeById(@PathVariable Long id) {
        PromoCode promoCode = promoCodeService.getPromoCodeById(id);
        return ResponseEntity.ok(new PromoCodeResponse(promoCode));
    }

    @GetMapping("/by-code/{code}")
    public ResponseEntity<PromoCodeResponse> getPromoCodeByCode(@PathVariable String code) {
        PromoCode promoCode = promoCodeService.getPromoCodeByCode(code);
        return ResponseEntity.ok(new PromoCodeResponse(promoCode));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromoCode(@PathVariable Long id) {
        promoCodeService.deletePromoCode(id);
        return ResponseEntity.noContent().build();
    }

}
