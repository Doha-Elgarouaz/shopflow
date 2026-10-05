package com.shopflow.stock.event;

public record OrderItemEvent(
        Long productId,
        Integer quantity
) {}
