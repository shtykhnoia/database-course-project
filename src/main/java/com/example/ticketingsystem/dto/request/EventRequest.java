package com.example.ticketingsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String description;

    @NotNull(message = "Organizer ID is required")
    private Long organizerId;

    private Long venueId;

    @NotNull(message = "Start datetime is required")
    private LocalDateTime startDatetime;

    private LocalDateTime endDatetime;

    @Pattern(regexp = "draft|published|cancelled|completed", message = "Status must be one of: draft, published, cancelled, completed")
    @Size(max = 20, message = "Status must not exceed 20 characters")
    private String eventStatus;
}
