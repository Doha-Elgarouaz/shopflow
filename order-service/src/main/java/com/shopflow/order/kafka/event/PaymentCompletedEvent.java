package com.shopflow.order.kafka.event;

import java.math.BigDecimal;

public record PaymentCompletedEvent(Long orderId, Long paymentId, BigDecimal amount, String transactionId, String customerId) {}
