package com.example.ticketingsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketCategory {
    private Long id;
    private Long eventId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantityAvailable;
    private LocalDateTime saleStartDate;
    private LocalDateTime saleEndDate;
}
