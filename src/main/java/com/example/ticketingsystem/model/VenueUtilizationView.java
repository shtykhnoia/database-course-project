package com.example.ticketingsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VenueUtilizationView {
    private Long venueId;
    private String venueName;
    private String address;
    private Integer capacity;
    private Long totalEvents;
    private Long activeEvents;
    private Long ticketsSold;
    private BigDecimal totalRevenue;
    private Integer avgOccupancyPercentage;
}
