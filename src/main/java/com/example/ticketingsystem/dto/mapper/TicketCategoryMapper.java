package com.example.ticketingsystem.dto.mapper;

import com.example.ticketingsystem.dto.request.TicketCategoryRequest;
import com.example.ticketingsystem.model.TicketCategory;
import org.springframework.stereotype.Component;

@Component
public class TicketCategoryMapper {

    public TicketCategory toEntity(TicketCategoryRequest request, Long eventId) {
        TicketCategory ticketCategory = new TicketCategory();
        ticketCategory.setEventId(eventId);
        ticketCategory.setName(request.getName());
        ticketCategory.setDescription(request.getDescription());
        ticketCategory.setPrice(request.getPrice());
        ticketCategory.setQuantityAvailable(request.getQuantityAvailable());
        ticketCategory.setSaleStartDate(request.getSaleStartDate());
        ticketCategory.setSaleEndDate(request.getSaleEndDate());
        return ticketCategory;
    }

    public void updateEntity(TicketCategory ticketCategory, TicketCategoryRequest request) {
        ticketCategory.setName(request.getName());
        ticketCategory.setDescription(request.getDescription());
        ticketCategory.setPrice(request.getPrice());
        ticketCategory.setQuantityAvailable(request.getQuantityAvailable());
        ticketCategory.setSaleStartDate(request.getSaleStartDate());
        ticketCategory.setSaleEndDate(request.getSaleEndDate());
    }
}
