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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
@Tag(name = "Заказы", description = "Создание и управление заказами билетов")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;
    private final PromoCodeService promoCodeService;

    @GetMapping
    @Operation(summary = "Получить все заказы",
               description = "Возвращает список всех заказов, можно фильтровать по статусу")
    @ApiResponse(responseCode = "200", description = "Список заказов")
    public ResponseEntity<List<OrderResponse>> getAllOrders(@Parameter(description = "Статус заказа (pending, confirmed, cancelled)") @RequestParam(required = false) String status) {
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
    @Operation(summary = "Создать заказ",
               description = "Создает новый заказ билетов. Автоматически резервирует билеты и создает платеж в статусе pending")
    @ApiResponse(responseCode = "201", description = "Заказ создан")
    @ApiResponse(responseCode = "400", description = "Недостаточно билетов или некорректные данные")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request.getUserId(), request.getItems());
        return ResponseEntity.status(HttpStatus.CREATED).body(new OrderResponse(order));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить заказ по ID", description = "Возвращает детальную информацию о заказе")
    @ApiResponse(responseCode = "200", description = "Заказ найден")
    @ApiResponse(responseCode = "404", description = "Заказ не найден")
    public ResponseEntity<OrderResponse> getOrder(@Parameter(description = "ID заказа") @PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(new OrderResponse(order));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Получить заказы пользователя", description = "Возвращает все заказы конкретного пользователя")
    @ApiResponse(responseCode = "200", description = "Список заказов пользователя")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@Parameter(description = "ID пользователя") @PathVariable Long userId) {
        List<Order> orders = orderService.getUserOrders(userId);
        List<OrderResponse> responses = orders.stream()
                .map(OrderResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/payment")
    @Operation(summary = "Оплатить заказ",
               description = "Подтверждает оплату заказа. Меняет статус заказа на confirmed и генерирует билеты")
    @ApiResponse(responseCode = "200", description = "Оплата успешна, заказ подтвержден")
    @ApiResponse(responseCode = "400", description = "Заказ не в статусе pending")
    @ApiResponse(responseCode = "404", description = "Заказ не найден")
    public ResponseEntity<OrderResponse> processPayment(
            @Parameter(description = "ID заказа") @PathVariable Long id,
            @Valid @RequestBody PaymentRequest request) {
        Order order = orderService.processPayment(id, request.getExternalPaymentId());
        return ResponseEntity.ok(new OrderResponse(order));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Отменить заказ",
               description = "Отменяет заказ и возвращает билеты в доступные. Нельзя отменить заказ с использованными билетами")
    @ApiResponse(responseCode = "200", description = "Заказ отменен")
    @ApiResponse(responseCode = "400", description = "Заказ уже отменен или содержит использованные билеты")
    public ResponseEntity<OrderResponse> cancelOrder(@Parameter(description = "ID заказа") @PathVariable Long id) {
        Order order = orderService.cancelOrder(id);
        return ResponseEntity.ok(new OrderResponse(order));
    }

    @GetMapping("/{id}/tickets")
    @Operation(summary = "Получить билеты заказа", description = "Возвращает все билеты, сгенерированные для данного заказа")
    @ApiResponse(responseCode = "200", description = "Список билетов")
    @ApiResponse(responseCode = "404", description = "Заказ не найден")
    public ResponseEntity<List<TicketResponse>> getOrderTickets(@Parameter(description = "ID заказа") @PathVariable Long id) {
        List<Ticket> tickets = orderService.getOrderTickets(id);
        List<TicketResponse> responses = tickets.stream()
                .map(TicketResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{orderId}/apply-promo")
    @Operation(summary = "Применить промокод",
               description = "Применяет промокод к заказу для получения скидки")
    @ApiResponse(responseCode = "200", description = "Промокод применен")
    @ApiResponse(responseCode = "400", description = "Промокод недействителен или истек")
    public ResponseEntity<OrderResponse> applyPromo(@Parameter(description = "ID заказа") @PathVariable Long orderId,
                                                    @Valid @RequestBody ApplyPromoCodeRequest request) {
        promoCodeService.applyPromoCode(request.getCode(), orderId);
        Order updatedOrder = orderService.getOrderById(orderId);
        return ResponseEntity.ok(new OrderResponse(updatedOrder));
    }
}
