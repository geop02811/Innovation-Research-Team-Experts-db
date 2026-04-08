package org.innov.expertdb.auth.dtos.notification;

import java.util.List;
import java.util.UUID;

public record MarkReadRequest(
        List<UUID> notificationIds
) {}