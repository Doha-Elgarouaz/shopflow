package com.shopflow.order.dto;

import com.shopflow.order.entity.Order;
import com.shopflow.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    Long id,
    String orderNumber,
    String customerId,
    String customerEmail,
    OrderStatus status,
    List<OrderItemResponse> items,
    BigDecimal totalAmount,
    String shippingAddress,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static OrderResponse from(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
            .map(item -> new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
            ))
            .toList();

        return new OrderResponse(
            order.getId(),
            order.getOrderNumber(),
            order.getCustomerId(),
            order.getCustomerEmail(),
            order.getStatus(),
            itemResponses,
            order.getTotalAmount(),
            order.getShippingAddress(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }
}
