package com.example.ticketingsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplyPromoCodeRequest {

    @NotBlank(message = "Promo code is required")
    private String code;
}
