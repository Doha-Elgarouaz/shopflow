package com.shopflow.stock.event;

import java.util.List;

public record OrderCancelledEvent(
        String orderId,
        String orderNumber,
        List<OrderItemEvent> items
) {}
