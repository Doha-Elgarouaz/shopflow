package com.shopflow.stock.event;

public record NotificationEvent(
        String recipientEmail,
        String recipientId,
        String type,
        String subject,
        String content,
        String referenceId
) {}
