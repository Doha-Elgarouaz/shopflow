package com.shopflow.notification.event;

public record OrderCancelledEvent(
        String orderId,
        String orderNumber,
        String customerId,
        String customerEmail,
        String reason
) {}
