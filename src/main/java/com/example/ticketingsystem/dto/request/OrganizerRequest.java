package com.example.ticketingsystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizerRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    private String description;

    @Email(message = "Invalid email format")
    private String contactEmail;

    @Size(max = 50, message = "Phone must not exceed 50 characters")
    private String contactPhone;

    private Long userId;
}
