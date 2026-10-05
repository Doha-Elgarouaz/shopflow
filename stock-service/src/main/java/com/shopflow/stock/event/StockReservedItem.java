package com.shopflow.stock.event;

public record StockReservedItem(
        Long productId,
        Integer reservedQty
) {}
