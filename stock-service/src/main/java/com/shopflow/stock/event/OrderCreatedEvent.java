package com.shopflow.stock.event;

import java.util.List;

public record OrderCreatedEvent(
        String orderId,
        String orderNumber,
        List<OrderItemEvent> items
) {}
