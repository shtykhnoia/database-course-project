package com.example.ticketingsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventSalesView {
    private Long eventId;
    private String eventTitle;
    private Long categoryId;
    private String categoryName;
    private BigDecimal categoryPrice;
    private Integer totalSold;
    private Integer quantityAvailable;
    private BigDecimal totalRevenue;
    private Integer soldLast7Days;
}
