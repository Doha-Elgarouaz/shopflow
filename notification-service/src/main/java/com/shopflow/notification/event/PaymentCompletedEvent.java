package com.shopflow.notification.event;

import java.math.BigDecimal;

public record PaymentCompletedEvent(
        String orderId,
        String paymentId,
        BigDecimal amount,
        String transactionId,
        String customerId,
        String customerEmail
) {}
