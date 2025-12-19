package com.example.ticketingsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long ticketCategoryId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private Long promoCodeId;
}
