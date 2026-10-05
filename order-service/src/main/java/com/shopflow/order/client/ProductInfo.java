package com.shopflow.order.client;

import java.math.BigDecimal;

public record ProductInfo(Long productId, String name, BigDecimal price, Integer stockQuantity) {}
