package com.shopflow.order.kafka.event;

import java.math.BigDecimal;
import java.util.List;

public record OrderCreatedEvent(
    Long orderId,
    String orderNumber,
    String customerId,
    String customerEmail,
    List<OrderItemEvent> items,
    BigDecimal totalAmount
) {}
