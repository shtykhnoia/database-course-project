package com.example.ticketingsystem.service;

import com.example.ticketingsystem.exception.ResourceNotFoundException;
import com.example.ticketingsystem.model.*;
import com.example.ticketingsystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;
    private final PaymentDAO paymentDAO;
    private final TicketDAO ticketDAO;
    private final TicketCategoryDAO ticketCategoryDAO;
    private final UserDAO userDAO;

    public OrderService(OrderDAO orderDAO, OrderItemDAO orderItemDAO,
                       PaymentDAO paymentDAO, TicketDAO ticketDAO,
                       TicketCategoryDAO ticketCategoryDAO, UserDAO userDAO) {
        this.orderDAO = orderDAO;
        this.orderItemDAO = orderItemDAO;
        this.paymentDAO = paymentDAO;
        this.ticketDAO = ticketDAO;
        this.ticketCategoryDAO = ticketCategoryDAO;
        this.userDAO = userDAO;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Order createOrder(Long userId, Long ticketCategoryId, int quantity) {
        userDAO.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        TicketCategory category = ticketCategoryDAO.findById(ticketCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket Category", ticketCategoryId));

        validateTicketPurchase(category, quantity);

        int updatedRows = ticketCategoryDAO.decreaseQuantity(ticketCategoryId, quantity);
        if (updatedRows == 0) {
            throw new IllegalStateException("Not enough tickets available");
        }

        BigDecimal totalAmount = category.getPrice().multiply(BigDecimal.valueOf(quantity));

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUserId(userId);
        order.setStatus("pending");
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(LocalDateTime.now());
        order = orderDAO.create(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setTicketCategoryId(ticketCategoryId);
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(category.getPrice());
        orderItemDAO.create(orderItem);

        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setAmount(totalAmount);
        payment.setStatus("pending");
        paymentDAO.create(payment);

        return order;
    }

    @Transactional
    public Order processPayment(Long orderId, String externalPaymentId) {
        Order order = orderDAO.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if (!"pending".equals(order.getStatus())) {
            throw new IllegalStateException("Order is not pending");
        }

        Payment payment = paymentDAO.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment for order", orderId));

        payment.setExternalPaymentId(externalPaymentId);
        payment.setStatus("succeeded");
        payment.setPaidAt(LocalDateTime.now());
        paymentDAO.updateStatus(payment.getId(), "succeeded");

        order.setStatus("confirmed");
        orderDAO.updateStatus(orderId, "confirmed");

        List<OrderItem> orderItems = orderItemDAO.findByOrderId(orderId);
        for (OrderItem item : orderItems) {
            generateTickets(item);
        }

        return order;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Order cancelOrder(Long orderId) {
        Order order = orderDAO.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if ("cancelled".equals(order.getStatus()) || "confirmed".equals(order.getStatus())) {
            throw new IllegalStateException("Cannot cancel order with status: " + order.getStatus());
        }

        List<OrderItem> orderItems = orderItemDAO.findByOrderId(orderId);
        for (OrderItem item : orderItems) {
            TicketCategory category = ticketCategoryDAO.findById(item.getTicketCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ticket Category", item.getTicketCategoryId()));

            category.setQuantityAvailable(category.getQuantityAvailable() + item.getQuantity());
            ticketCategoryDAO.update(category);
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

    public List<Order> getUserOrders(Long userId) {
        return orderDAO.findByUserId(userId);
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

    private void validateTicketPurchase(TicketCategory category, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
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

    private void generateTickets(OrderItem orderItem) {
        for (int i = 0; i < orderItem.getQuantity(); i++) {
            Ticket ticket = new Ticket();
            ticket.setTicketCode(generateTicketCode());
            ticket.setOrderItemId(orderItem.getId());
            ticket.setStatus("active");
            ticketDAO.create(ticket);
        }
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateTicketCode() {
        return "TKT-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
