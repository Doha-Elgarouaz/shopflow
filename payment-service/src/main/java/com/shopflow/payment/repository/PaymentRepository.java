package com.shopflow.payment.repository;

import com.shopflow.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);
    Page<Payment> findByCustomerId(String customerId, Pageable pageable);
    Optional<Payment> findByPaymentReference(String paymentReference);
    boolean existsByOrderId(Long orderId);
}
