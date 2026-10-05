package com.shopflow.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Name cannot be blank")
        String name,

        String description,

        @NotBlank(message = "SKU cannot be blank")
        String sku,

        @NotNull(message = "Price cannot be null")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
        BigDecimal price,

        @NotNull(message = "Stock quantity cannot be null")
        @PositiveOrZero(message = "Stock quantity cannot be negative")
        Integer stockQuantity,

        @NotBlank(message = "Category cannot be blank")
        String category,

        String imageUrl,
        
        Boolean active
) {}
