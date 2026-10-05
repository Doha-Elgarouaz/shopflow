package com.shopflow.stock.event;

import java.util.List;

public record StockReservedEvent(
        String orderId,
        List<StockReservedItem> items
) {}
