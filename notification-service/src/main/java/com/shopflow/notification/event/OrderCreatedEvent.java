package com.shopflow.notification.event;

import java.math.BigDecimal;

public record OrderCreatedEvent(
        String orderId,
        String orderNumber,
        String customerId,
        String customerEmail,
        BigDecimal totalAmount
) {}
