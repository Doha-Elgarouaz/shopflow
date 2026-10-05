package com.shopflow.payment.kafka.event;

public record PaymentFailedEvent(
    Long orderId,
    Long paymentId,
    String reason,
    String customerId
) {}
