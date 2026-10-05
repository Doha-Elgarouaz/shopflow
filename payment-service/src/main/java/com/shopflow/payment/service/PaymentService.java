package com.shopflow.payment.service;

import com.shopflow.payment.dto.PageResponse;
import com.shopflow.payment.dto.PaymentRequest;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;
    private final Random random = new Random();

    /**
     * Triggered automatically by Kafka when an OrderCreatedEvent is received.
     * Simulates payment processing with 80% success rate.
     */
    @Transactional
    public void processPaymentForOrder(OrderCreatedEvent event) {
        // Idempotency check — avoid duplicate payments
        if (paymentRepository.existsByOrderId(event.orderId())) {
            log.warn("Payment already exists for orderId={}, skipping.", event.orderId());
            return;
        }

        Payment payment = Payment.builder()
                .orderId(event.orderId())
                .customerId(event.customerId())
                .amount(event.totalAmount())
                .method(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PROCESSING)
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment {} created for orderId={}, processing...", saved.getPaymentReference(), event.orderId());

        simulateProcessing(saved, event.customerId());
    }

    /**
     * Manual payment endpoint — for clients who want to pay explicitly.
     */
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request, String customerId) {
        if (paymentRepository.existsByOrderId(request.orderId())) {
            return paymentRepository.findByOrderId(request.orderId())
                    .map(PaymentResponse::from)
                    .orElseThrow(() -> new PaymentNotFoundException("Payment not found for orderId: " + request.orderId()));
        }

        Payment payment = Payment.builder()
                .orderId(request.orderId())
                .customerId(customerId)
                .amount(request.amount())
                .method(request.method())
                .cardLastFour(request.cardLastFour())
                .status(PaymentStatus.PROCESSING)
                .build();

        Payment saved = paymentRepository.save(payment);
        simulateProcessing(saved, customerId);
        return PaymentResponse.from(paymentRepository.findById(saved.getId()).orElse(saved));
    }

    private void simulateProcessing(Payment payment, String customerId) {
        try {
            Thread.sleep(300); // Simulate processing delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean success = random.nextDouble() < 0.80; // 80% success rate

        if (success) {
            payment.setTransactionId(UUID.randomUUID().toString());
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setProcessedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            log.info("Payment COMPLETED for orderId={}, txId={}", payment.getOrderId(), payment.getTransactionId());
            paymentEventProducer.publishPaymentCompleted(new PaymentCompletedEvent(
                    payment.getOrderId(), payment.getId(), payment.getAmount(),
                    payment.getTransactionId(), customerId
            ));
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Insufficient funds or card declined");
            payment.setProcessedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            log.warn("Payment FAILED for orderId={}", payment.getOrderId());
            paymentEventProducer.publishPaymentFailed(new PaymentFailedEvent(
                    payment.getOrderId(), payment.getId(),
                    "Insufficient funds or card declined", customerId
            ));
        }
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(PaymentResponse::from)
                .orElseThrow(() -> new PaymentNotFoundException("No payment found for orderId: " + orderId));
    }

    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getPaymentsByCustomer(String customerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Payment> payments = paymentRepository.findByCustomerId(customerId, pageable);
        List<PaymentResponse> content = payments.getContent().stream().map(PaymentResponse::from).toList();
        return new PageResponse<>(content, page, size, payments.getTotalElements(), payments.getTotalPages(), payments.isLast());
    }

    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getAllPayments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Payment> payments = paymentRepository.findAll(pageable);
        List<PaymentResponse> content = payments.getContent().stream().map(PaymentResponse::from).toList();
        return new PageResponse<>(content, page, size, payments.getTotalElements(), payments.getTotalPages(), payments.isLast());
    }

    @Transactional
    public PaymentResponse refundPayment(Long paymentId, String customerId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        if (!payment.getCustomerId().equals(customerId)) {
            throw new IllegalStateException("Access denied: payment does not belong to this customer");
        }
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Only COMPLETED payments can be refunded, current status: " + payment.getStatus());
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setProcessedAt(LocalDateTime.now());
        return PaymentResponse.from(paymentRepository.save(payment));
    }
}
