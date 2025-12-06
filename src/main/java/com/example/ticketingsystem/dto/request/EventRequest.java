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

    @NotNull(message = "Start datetime is required")
    private LocalDateTime startDatetime;

    @Pattern(regexp = "draft|published|cancelled", message = "Status must be one of: draft, published, cancelled")
    @Size(max = 20, message = "Status must not exceed 20 characters")
    private String eventStatus;
}
