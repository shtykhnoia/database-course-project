package com.example.ticketingsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Organizer {
    private Long id;
    private String name;
    private String description;
    private String contactEmail;
    private String contactPhone;
    private Long userId;
}
