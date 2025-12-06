package com.example.ticketingsystem.dto.response;

import com.example.ticketingsystem.model.TicketCategory;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TicketCategoryResponse {
    private Long id;
    private Long eventId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantityAvailable;
    private LocalDateTime saleStartDate;
    private LocalDateTime saleEndDate;

    public TicketCategoryResponse(TicketCategory ticketCategory) {
        this.id = ticketCategory.getId();
        this.eventId = ticketCategory.getEventId();
        this.name = ticketCategory.getName();
        this.description = ticketCategory.getDescription();
        this.price = ticketCategory.getPrice();
        this.quantityAvailable = ticketCategory.getQuantityAvailable();
        this.saleStartDate = ticketCategory.getSaleStartDate();
        this.saleEndDate = ticketCategory.getSaleEndDate();
    }
}
