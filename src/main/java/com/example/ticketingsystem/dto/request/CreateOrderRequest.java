package com.example.ticketingsystem.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Ticket category ID is required")
    private Long ticketCategoryId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
