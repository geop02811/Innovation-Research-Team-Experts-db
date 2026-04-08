package org.innov.expertdb.auth.dtos.notification;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String title,
        String message,
        boolean isRead,
        String notificationType,
        String relatedEntityId,
        LocalDateTime createdAt
) {}