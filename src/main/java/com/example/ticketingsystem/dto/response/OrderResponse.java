package com.example.ticketingsystem.dto.response;

import com.example.ticketingsystem.model.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long userId;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.orderNumber = order.getOrderNumber();
        this.userId = order.getUserId();
        this.status = order.getStatus();
        this.totalAmount = order.getTotalAmount();
        this.createdAt = order.getCreatedAt();
    }
}
