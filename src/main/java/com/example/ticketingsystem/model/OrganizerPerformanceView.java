package com.example.ticketingsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizerPerformanceView {
    private Long organizerId;
    private String organizerName;
    private Integer totalEvents;
    private Integer publishedEvents;
    private Integer cancelledEvents;
    private Integer totalTicketsSold;
    private BigDecimal totalRevenue;
    private BigDecimal avgTicketPrice;
    private BigDecimal successRate;
}
