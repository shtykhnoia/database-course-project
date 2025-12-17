package com.example.ticketingsystem.service;

import com.example.ticketingsystem.exception.ResourceNotFoundException;
import com.example.ticketingsystem.model.Order;
import com.example.ticketingsystem.model.OrderItem;
import com.example.ticketingsystem.model.PromoCode;
import com.example.ticketingsystem.model.TicketCategory;
import com.example.ticketingsystem.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PromoCodeService {
    private final PromoCodeDAO promoCodeDAO;
    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;
    private final TicketCategoryDAO ticketCategoryDAO;

    public PromoCode createPromocode(PromoCode promoCode) {
        return promoCodeDAO.createPromoCode(promoCode);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void applyPromoCode(String code, Long orderId) {
        PromoCode promoCode = promoCodeDAO.findByCode(code).orElseThrow(
                () -> new ResourceNotFoundException("Promo code not found")
        );

        Order order = orderDAO.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Order not found")
        );

        List<OrderItem> orderItems = orderItemDAO.findByOrderId(orderId);
        List<OrderItem> discountItems = orderItems.stream()
                .filter(it -> {
                    TicketCategory category = ticketCategoryDAO.findById(it.getTicketCategoryId())
                            .orElseThrow(() -> new ResourceNotFoundException("TicketCategory", it.getTicketCategoryId()));
                    Long eventId = category.getEventId();

                    return promoCode.getEventId() == null || promoCode.getEventId().equals(eventId);
                })
                .toList();
        if (discountItems.isEmpty()) {
            throw new IllegalArgumentException("Promo code is not acceptable to any items in order");
        }
        validatePromoCode(promoCode);
        int updated = promoCodeDAO.incrementUsedCount(promoCode.getId());

        if (updated == 0) {
            throw new IllegalArgumentException("Promo code usage limit exceeded");
        }

        BigDecimal totalDiscount = BigDecimal.ZERO;
        for (OrderItem item: discountItems) {
            BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal itemDiscount = calculateDiscount(promoCode, itemTotal);
            totalDiscount = totalDiscount.add(itemDiscount);
        }

        BigDecimal newPrice = order.getTotalAmount().subtract(totalDiscount);
        orderDAO.updateTotalAmount(orderId, newPrice);
    }

    @Transactional
    public void deletePromoCode(Long id) {
        int deleted = promoCodeDAO.deletePromoCode(id);  // вернуть int из DAO
        if (deleted == 0) {
            throw new ResourceNotFoundException("Promo code", id);
        }
    }

    public List<PromoCode> getAllPromoCodes() {
        return promoCodeDAO.getAllPromoCodes();
    }

    public PromoCode getPromoCodeById(Long id) {
        return promoCodeDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code", id));
    }

    public PromoCode getPromoCodeByCode(String code) {
        return promoCodeDAO.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code with code: " + code));
    }

    public List<PromoCode> getPromoCodesByEventId(Long eventId) {
        return promoCodeDAO.findByEventId(eventId);
    }

    private void validatePromoCode(PromoCode promoCode) {
        if (promoCode.getValidUntil() != null && promoCode.getValidUntil().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Promo code has expired");
        }

        if (promoCode.getValidFrom() != null && promoCode.getValidFrom().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Promo code is not valid yet");
        }
    }

    private BigDecimal calculateDiscount(PromoCode promoCode, BigDecimal amount) {
        if (promoCode.getDiscountType().equals("percent")) {
            return amount.multiply(promoCode.getDiscountValue().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        }
        return promoCode.getDiscountValue();
    }

}
