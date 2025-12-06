package com.example.ticketingsystem.dto.response;

import com.example.ticketingsystem.model.Ticket;
import lombok.Data;

@Data
public class TicketResponse {
    private Long id;
    private String ticketCode;
    private String attendeeName;
    private String attendeeEmail;
    private String status;

    public TicketResponse(Ticket ticket) {
        this.id = ticket.getId();
        this.ticketCode = ticket.getTicketCode();
        this.attendeeName = ticket.getAttendeeName();
        this.attendeeEmail = ticket.getAttendeeEmail();
        this.status = ticket.getStatus();
    }
}
