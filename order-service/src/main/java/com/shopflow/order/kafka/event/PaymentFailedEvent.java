package com.shopflow.order.kafka.event;

public record PaymentFailedEvent(Long orderId, Long paymentId, String reason, String customerId) {}
