package com.example.ticketingsystem.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PromoCodeRequest {

    @NotBlank
    @Size(max = 50)
    private String code;

    private Long eventId;

    @Pattern(regexp = "percent|fixed", message = "Discount type must be 'percent' or 'fixed'")
    @NotBlank
    private String discountType;

    @DecimalMin(value = "0.0", message = "Discount value must be positive")
    @NotNull
    private BigDecimal discountValue;

    @Min(value = 1, message = "max uses must be at least 1")
    private int maxUses;

    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
}
