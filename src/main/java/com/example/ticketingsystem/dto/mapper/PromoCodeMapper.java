package com.example.ticketingsystem.dto.mapper;

import com.example.ticketingsystem.dto.request.PromoCodeRequest;
import com.example.ticketingsystem.model.PromoCode;
import org.springframework.stereotype.Component;

@Component
public class PromoCodeMapper {

    public PromoCode toEntity(PromoCodeRequest request) {
        PromoCode promoCode = new PromoCode();
        promoCode.setCode(request.getCode());
        promoCode.setEventId(request.getEventId());
        promoCode.setDiscountType(request.getDiscountType());
        promoCode.setDiscountValue(request.getDiscountValue());
        promoCode.setMaxUses(request.getMaxUses());
        promoCode.setUsedCount(0);
        promoCode.setValidFrom(request.getValidFrom());
        promoCode.setValidUntil(request.getValidUntil());
        return promoCode;
    }

    public void updateEntity(PromoCode promoCode, PromoCodeRequest request) {
        promoCode.setCode(request.getCode());
        promoCode.setEventId(request.getEventId());
        promoCode.setDiscountType(request.getDiscountType());
        promoCode.setDiscountValue(request.getDiscountValue());
        promoCode.setMaxUses(request.getMaxUses());
        promoCode.setValidFrom(request.getValidFrom());
        promoCode.setValidUntil(request.getValidUntil());
    }
}
