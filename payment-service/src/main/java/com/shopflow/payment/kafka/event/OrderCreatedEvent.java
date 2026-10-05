package com.shopflow.payment.kafka.event;

import java.math.BigDecimal;

public record OrderCreatedEvent(
    Long orderId,
    String orderNumber,
    String customerId,
    String customerEmail,
    BigDecimal totalAmount
) {}
