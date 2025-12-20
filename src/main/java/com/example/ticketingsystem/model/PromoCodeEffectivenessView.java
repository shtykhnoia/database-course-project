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
    private String discountType;
    private BigDecimal discountValue;
    private Integer maxUses;
    private Integer usedCount;
    private Long eventId;
    private String eventTitle;
    private Long ordersWithPromo;
    private BigDecimal totalSalesWithPromo;
    private BigDecimal totalDiscountGiven;
    private BigDecimal avgDiscountPerOrder;
    private Integer usagePercentage;
    private String promoStatus;
}
