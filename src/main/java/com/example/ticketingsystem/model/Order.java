package com.example.ticketingsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Long id;
    private String orderNumber;
    private Long userId;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
