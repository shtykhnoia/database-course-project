package com.example.ticketingsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    private Long id;
    private String ticketCode;
    private Long orderItemId;
    private String attendeeName;
    private String attendeeEmail;
    private String status;
}
