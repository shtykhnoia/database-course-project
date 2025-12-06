package com.example.ticketingsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private Long id;
    private Long orderId;
    private String externalPaymentId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime paidAt;
}
