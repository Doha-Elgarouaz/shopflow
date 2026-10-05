package com.shopflow.order.kafka.event;

import java.math.BigDecimal;

public record OrderItemEvent(Long productId, Integer quantity, BigDecimal unitPrice) {}
