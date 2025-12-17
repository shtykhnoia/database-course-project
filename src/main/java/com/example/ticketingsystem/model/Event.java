package com.example.ticketingsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private Long id;
    private String title;
    private String description;
    private Long organizerId;
    private Long venueId;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private String eventStatus;
}
