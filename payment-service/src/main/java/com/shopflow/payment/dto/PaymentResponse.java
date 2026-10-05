package com.shopflow.payment.dto;

import com.shopflow.payment.entity.Payment;
import com.shopflow.payment.entity.PaymentMethod;
import com.shopflow.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
    Long id,
    String paymentReference,
    Long orderId,
    String customerId,
    BigDecimal amount,
    PaymentMethod method,
    PaymentStatus status,
    String transactionId,
    String failureReason,
    String cardLastFour,
    LocalDateTime createdAt,
    LocalDateTime processedAt
) {
    public static PaymentResponse from(Payment p) {
        return new PaymentResponse(
            p.getId(), p.getPaymentReference(), p.getOrderId(), p.getCustomerId(),
            p.getAmount(), p.getMethod(), p.getStatus(), p.getTransactionId(),
            p.getFailureReason(), p.getCardLastFour(), p.getCreatedAt(), p.getProcessedAt()
        );
    }
}
