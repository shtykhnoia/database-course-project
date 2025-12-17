package com.example.ticketingsystem.dto.response;

import com.example.ticketingsystem.model.PromoCode;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PromoCodeResponse {
    private Long id;
    private String code;
    private Long eventId;
    private String discountType;
    private BigDecimal discountValue;
    private Integer maxUses;
    private Integer usedCount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;

    public PromoCodeResponse(PromoCode promoCode) {
        this.id = promoCode.getId();
        this.code = promoCode.getCode();
        this.eventId = promoCode.getEventId();
        this.discountType = promoCode.getDiscountType();
        this.discountValue = promoCode.getDiscountValue();
        this.maxUses = promoCode.getMaxUses();
        this.usedCount = promoCode.getUsedCount();
        this.validFrom = promoCode.getValidFrom();
        this.validUntil = promoCode.getValidUntil();
    }
}
