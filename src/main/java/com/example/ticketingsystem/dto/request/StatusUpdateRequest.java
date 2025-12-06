package com.example.ticketingsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "draft|published|cancelled", message = "Status must be one of: draft, published, cancelled")
    private String status;
}
