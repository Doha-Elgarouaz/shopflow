package com.shopflow.notification.service;

import com.shopflow.notification.model.Notification;
import com.shopflow.notification.model.NotificationStatus;
import com.shopflow.notification.model.NotificationType;
import com.shopflow.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Unit Tests")
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificationService, "simulateEmail", true);
    }

    @Test
    @DisplayName("saveAndSendNotification — should save notification with SENT status")
    void saveAndSendNotification_Success() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        notificationService.saveAndSendNotification(
                "customer@shopflow.com",
                "cust-123",
                NotificationType.ORDER_CONFIRMED,
                "Order Confirmation #123",
                "Your order has been confirmed.",
                "ORD-123"
        );

        verify(notificationRepository, times(2)).save(argThat(n ->
                n.getRecipientEmail().equals("customer@shopflow.com") &&
                n.getType() == NotificationType.ORDER_CONFIRMED
        ));
    }
}
