package com.shopflow.order.kafka.event;

public record OrderCancelledEvent(
    Long orderId,
    String orderNumber,
    String customerId,
    String customerEmail,
    String reason
) {}
