package com.shopflow.notification.service;

import com.shopflow.notification.dto.NotificationResponse;
import com.shopflow.notification.model.Notification;
import com.shopflow.notification.model.NotificationStatus;
import com.shopflow.notification.model.NotificationType;
import com.shopflow.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Value("${notification.email.simulation:true}")
    private boolean simulateEmail;

    public void saveAndSendNotification(String recipientEmail, String recipientId, NotificationType type, String subject, String content, String referenceId) {
        Notification notification = Notification.builder()
                .recipientEmail(recipientEmail)
                .recipientId(recipientId)
                .type(type)
                .subject(subject)
                .content(content)
                .referenceId(referenceId)
                .status(NotificationStatus.PENDING)
                .build();
                
        notificationRepository.save(notification);
        
        try {
            if (simulateEmail) {
                log.info("[EMAIL SIMULATION] Sending email to: {}", recipientEmail);
                log.info("[EMAIL SIMULATION] Subject: {}", subject);
                log.info("[EMAIL SIMULATION] Content: {}", content);
            } else {
                // actual mail sender logic could go here
                log.info("Sending actual email to {}", recipientEmail);
            }
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            log.error("Failed to send email to {}", recipientEmail, e);
            notification.setStatus(NotificationStatus.FAILED);
        }
        
        notificationRepository.save(notification);
    }

    public Page<NotificationResponse> getNotificationsByRecipient(String recipientId, int page, int size) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId, PageRequest.of(page, size))
                .map(this::mapToResponse);
    }

    public Page<NotificationResponse> getAllNotifications(int page, int size) {
        return notificationRepository.findAll(PageRequest.of(page, size))
                .map(this::mapToResponse);
    }
    
    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .recipientEmail(notification.getRecipientEmail())
                .recipientId(notification.getRecipientId())
                .type(notification.getType())
                .subject(notification.getSubject())
                .content(notification.getContent())
                .status(notification.getStatus())
                .referenceId(notification.getReferenceId())
                .createdAt(notification.getCreatedAt())
                .sentAt(notification.getSentAt())
                .build();
    }
}
