package com.example.ticketingsystem.controller;

import com.example.ticketingsystem.dto.request.ApplyPromoCodeRequest;
import com.example.ticketingsystem.dto.request.CreateOrderRequest;
import com.example.ticketingsystem.dto.request.PaymentRequest;
import com.example.ticketingsystem.dto.response.OrderResponse;
import com.example.ticketingsystem.dto.response.TicketResponse;
import com.example.ticketingsystem.model.Order;
import com.example.ticketingsystem.model.Ticket;
import com.example.ticketingsystem.service.OrderService;
import com.example.ticketingsystem.service.PromoCodeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PromoCodeService promoCodeService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(@RequestParam(required = false) String status) {
        List<Order> orders;
        if (status != null) {
            orders = orderService.getOrdersByStatus(status);
        } else {
            orders = orderService.getAllOrders();
        }
        List<OrderResponse> responses = orders.stream()
                .map(OrderResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(
                request.getUserId(),
                request.getTicketCategoryId(),
                request.getQuantity()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(new OrderResponse(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(new OrderResponse(order));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable Long userId) {
        List<Order> orders = orderService.getUserOrders(userId);
        List<OrderResponse> responses = orders.stream()
                .map(OrderResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/payment")
    public ResponseEntity<OrderResponse> processPayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentRequest request) {
        Order order = orderService.processPayment(id, request.getExternalPaymentId());
        return ResponseEntity.ok(new OrderResponse(order));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        Order order = orderService.cancelOrder(id);
        return ResponseEntity.ok(new OrderResponse(order));
    }

    @GetMapping("/{id}/tickets")
    public ResponseEntity<List<TicketResponse>> getOrderTickets(@PathVariable Long id) {
        List<Ticket> tickets = orderService.getOrderTickets(id);
        List<TicketResponse> responses = tickets.stream()
                .map(TicketResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{orderId}/apply-promo")
    public ResponseEntity<OrderResponse> applyPromo(@PathVariable Long orderId,
                                                    @Valid @RequestBody ApplyPromoCodeRequest request) {
        promoCodeService.applyPromoCode(request.getCode(), orderId);
        Order updatedOrder = orderService.getOrderById(orderId);
        return ResponseEntity.ok(new OrderResponse(updatedOrder));
    }
}
