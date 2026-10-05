package com.shopflow.notification.dto;

import com.shopflow.notification.model.NotificationStatus;
import com.shopflow.notification.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private Long id;
    private String recipientEmail;
    private String recipientId;
    private NotificationType type;
    private String subject;
    private String content;
    private NotificationStatus status;
    private String referenceId;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
}
