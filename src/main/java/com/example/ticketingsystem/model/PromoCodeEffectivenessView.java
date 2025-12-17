package com.example.ticketingsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromoCodeEffectivenessView {
    private Long promoCodeId;
    private String code;
    private Long eventId;
    private String eventTitle;
    private String discountType;
    private BigDecimal discountValue;
    private Integer ordersWithPromo;
    private BigDecimal totalDiscountGiven;
    private BigDecimal avgDiscountPerOrder;
}
