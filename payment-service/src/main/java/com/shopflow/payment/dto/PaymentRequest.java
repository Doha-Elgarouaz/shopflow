package com.shopflow.payment.dto;

import com.shopflow.payment.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequest(
    @NotNull Long orderId,
    @NotNull @Positive BigDecimal amount,
    @NotNull PaymentMethod method,
    String cardLastFour
) {}
