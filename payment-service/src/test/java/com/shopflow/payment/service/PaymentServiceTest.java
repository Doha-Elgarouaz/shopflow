package com.shopflow.payment.service;

import com.shopflow.payment.dto.PaymentResponse;
import com.shopflow.payment.entity.Payment;
import com.shopflow.payment.entity.PaymentMethod;
import com.shopflow.payment.entity.PaymentStatus;
import com.shopflow.payment.exception.PaymentNotFoundException;
import com.shopflow.payment.kafka.PaymentEventProducer;
import com.shopflow.payment.kafka.event.OrderCreatedEvent;
import com.shopflow.payment.kafka.event.PaymentCompletedEvent;
import com.shopflow.payment.kafka.event.PaymentFailedEvent;
import com.shopflow.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Unit Tests")
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private PaymentEventProducer paymentEventProducer;

    @InjectMocks
    private PaymentService paymentService;

    private Payment completedPayment;
    private OrderCreatedEvent orderEvent;

    @BeforeEach
    void setUp() {
        completedPayment = Payment.builder()
                .id(1L)
                .paymentReference("ref-abc-123")
                .orderId(10L)
                .customerId("user-123")
                .amount(new BigDecimal("149.99"))
                .method(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.COMPLETED)
                .transactionId("txn-xyz-789")
                .createdAt(LocalDateTime.now())
                .processedAt(LocalDateTime.now())
                .build();

        orderEvent = new OrderCreatedEvent(
                10L, "ORD-TEST1234", "user-123", "test@shopflow.com",
                new BigDecimal("149.99")
        );
    }

    @Test
    @DisplayName("processPaymentForOrder — should skip if payment already exists (idempotency)")
    void processPaymentForOrder_WhenAlreadyExists_ShouldSkip() {
        when(paymentRepository.existsByOrderId(10L)).thenReturn(true);

        paymentService.processPaymentForOrder(orderEvent);

        verify(paymentRepository, never()).save(any());
        verifyNoInteractions(paymentEventProducer);
    }

    @Test
    @DisplayName("getPaymentByOrderId — should return payment response when found")
    void getPaymentByOrderId_WhenFound_ShouldReturnResponse() {
        when(paymentRepository.findByOrderId(10L)).thenReturn(Optional.of(completedPayment));

        PaymentResponse response = paymentService.getPaymentByOrderId(10L);

        assertThat(response.orderId()).isEqualTo(10L);
        assertThat(response.status()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(response.transactionId()).isEqualTo("txn-xyz-789");
    }

    @Test
    @DisplayName("getPaymentByOrderId — should throw when payment not found")
    void getPaymentByOrderId_WhenNotFound_ShouldThrow() {
        when(paymentRepository.findByOrderId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPaymentByOrderId(999L))
                .isInstanceOf(PaymentNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("refundPayment — should set status to REFUNDED")
    void refundPayment_WhenCompleted_ShouldRefund() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(completedPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(completedPayment);

        PaymentResponse response = paymentService.refundPayment(1L, "user-123");

        verify(paymentRepository).save(argThat(p -> p.getStatus() == PaymentStatus.REFUNDED));
    }

    @Test
    @DisplayName("refundPayment — should throw when customer doesn't own payment")
    void refundPayment_WhenWrongCustomer_ShouldThrow() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(completedPayment));

        assertThatThrownBy(() -> paymentService.refundPayment(1L, "other-user"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Access denied");
    }

    @Test
    @DisplayName("refundPayment — should throw when payment is not COMPLETED")
    void refundPayment_WhenNotCompleted_ShouldThrow() {
        completedPayment.setStatus(PaymentStatus.FAILED);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(completedPayment));

        assertThatThrownBy(() -> paymentService.refundPayment(1L, "user-123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("COMPLETED");
    }
}
