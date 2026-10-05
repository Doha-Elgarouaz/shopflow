package com.shopflow.product.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String sku,
        BigDecimal price,
        Integer stockQuantity,
        String category,
        String imageUrl,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {}
