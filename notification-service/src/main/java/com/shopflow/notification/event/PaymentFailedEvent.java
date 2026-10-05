package com.shopflow.notification.event;

public record PaymentFailedEvent(
        String orderId,
        String paymentId,
        String reason,
        String customerId,
        String customerEmail
) {}
