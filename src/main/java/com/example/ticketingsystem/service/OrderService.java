package com.example.ticketingsystem.service;

import com.example.ticketingsystem.dto.request.OrderItemRequest;
import com.example.ticketingsystem.exception.ResourceNotFoundException;
import com.example.ticketingsystem.model.*;
import com.example.ticketingsystem.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;
    private final PaymentDAO paymentDAO;
    private final TicketDAO ticketDAO;
    private final TicketCategoryDAO ticketCategoryDAO;
    private final UserDAO userDAO;
    private final PromoCodeDAO promoCodeDAO;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Order createOrder(Long userId, List<OrderItemRequest> items) {
        userDAO.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<ValidatedOrderItem> validatedItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : items) {
            TicketCategory category = ticketCategoryDAO.findById(itemRequest.getTicketCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ticket Category", itemRequest.getTicketCategoryId()));

            validateTicketPurchase(category, itemRequest.getQuantity());

            int updatedRows = ticketCategoryDAO.decreaseQuantity(itemRequest.getTicketCategoryId(), itemRequest.getQuantity());
            if (updatedRows == 0) {
                throw new IllegalStateException("Not enough tickets available for category: " + category.getName());
            }

            BigDecimal itemTotal = category.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            validatedItems.add(new ValidatedOrderItem(
                    itemRequest.getTicketCategoryId(),
                    itemRequest.getQuantity(),
                    category.getPrice()
            ));
        }

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUserId(userId);
        order.setStatus("pending");
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(LocalDateTime.now());
        order = orderDAO.create(order);

        for (ValidatedOrderItem validated : validatedItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setTicketCategoryId(validated.ticketCategoryId());
            orderItem.setQuantity(validated.quantity());
            orderItem.setUnitPrice(validated.unitPrice());
            orderItemDAO.create(orderItem);
        }

        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setAmount(totalAmount);
        payment.setStatus("pending");
        paymentDAO.create(payment);

        return order;
    }

    private record ValidatedOrderItem(Long ticketCategoryId, Integer quantity, BigDecimal unitPrice) {}

    @Transactional
    public Order processPayment(Long orderId, String externalPaymentId) {
        Order order = orderDAO.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if (!"pending".equals(order.getStatus())) {
            throw new IllegalStateException("Order is not pending");
        }

        Payment payment = paymentDAO.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment for order", orderId));

        paymentDAO.updatePayment(payment.getId(), "succeeded", externalPaymentId, LocalDateTime.now());

        order.setStatus("confirmed");
        orderDAO.updateStatus(orderId, "confirmed");

        List<OrderItem> orderItems = orderItemDAO.findByOrderId(orderId);
        List<Ticket> ticketsToCreate = new ArrayList<>();
        for (OrderItem item : orderItems) {
            for (int i = 0; i < item.getQuantity(); i++) {
                Ticket ticket = new Ticket();
                ticket.setTicketCode(generateTicketCode());
                ticket.setOrderItemId(item.getId());
                ticket.setStatus("active");
                ticketsToCreate.add(ticket);
            }
        }
        if (!ticketsToCreate.isEmpty()) {
            ticketDAO.batchCreate(ticketsToCreate);
        }

        return order;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Order cancelOrder(Long orderId) {
        Order order = orderDAO.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if ("cancelled".equals(order.getStatus())) {
            throw new IllegalStateException("Order is already cancelled");
        }

        if ("confirmed".equals(order.getStatus())) {
            List<Ticket> tickets = getOrderTickets(orderId);
            for (Ticket ticket : tickets) {
                if ("checked_in".equals(ticket.getStatus())) {
                    throw new IllegalStateException("Cannot cancel order with checked-in tickets");
                }
            }
        }

        List<OrderItem> orderItems = orderItemDAO.findByOrderId(orderId);

        for (OrderItem item : orderItems) {
            int updated = ticketCategoryDAO.increaseQuantity(item.getTicketCategoryId(), item.getQuantity());
            if (updated == 0) {
                throw new IllegalStateException("Ticket category not found: " + item.getTicketCategoryId());
            }

            if (item.getPromoCodeId() != null) {
                promoCodeDAO.decrementUsedCount(item.getPromoCodeId());
            }
        }

        List<Ticket> tickets = getOrderTickets(orderId);
        if (!tickets.isEmpty()) {
            List<Long> ticketIds = tickets.stream().map(Ticket::getId).toList();
            ticketDAO.batchUpdateStatus(ticketIds, "cancelled");
        }

        order.setStatus("cancelled");
        orderDAO.updateStatus(orderId, "cancelled");

        Payment payment = paymentDAO.findByOrderId(orderId).orElse(null);
        if (payment != null && "pending".equals(payment.getStatus())) {
            paymentDAO.updateStatus(payment.getId(), "failed");
        }

        return order;
    }

    public Order getOrderById(Long orderId) {
        return orderDAO.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    }

    public List<Order> getAllOrders() {
        return orderDAO.findAll();
    }

    public List<Order> getUserOrders(Long userId) {
        return orderDAO.findByUserId(userId);
    }

    public List<Order> getOrdersByStatus(String status) {
        return orderDAO.findByStatus(status);
    }

    public List<Ticket> getOrderTickets(Long orderId) {
        orderDAO.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        List<OrderItem> orderItems = orderItemDAO.findByOrderId(orderId);
        List<Ticket> allTickets = new ArrayList<>();

        for (OrderItem item : orderItems) {
            allTickets.addAll(ticketDAO.findByOrderItemId(item.getId()));
        }

        return allTickets;
    }

    private static final int MAX_TICKETS_PER_ORDER = 10;

    private void validateTicketPurchase(TicketCategory category, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        if (quantity > MAX_TICKETS_PER_ORDER) {
            throw new IllegalArgumentException("Cannot purchase more than " + MAX_TICKETS_PER_ORDER + " tickets per order");
        }

        if (category.getQuantityAvailable() < quantity) {
            throw new IllegalArgumentException("Not enough tickets available");
        }

        LocalDateTime now = LocalDateTime.now();
        if (category.getSaleStartDate() != null && now.isBefore(category.getSaleStartDate())) {
            throw new IllegalArgumentException("Ticket sales have not started yet");
        }

        if (category.getSaleEndDate() != null && now.isAfter(category.getSaleEndDate())) {
            throw new IllegalArgumentException("Ticket sales have ended");
        }
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateTicketCode() {
        return "TKT-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
