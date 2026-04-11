package org.innov.expertdb.auth.dtos.admin;

import java.util.List;

public record AdminNotificationsResponse(
        int pendingCount,
        List<PendingUserNotification> pendingUsers
) {
    public record PendingUserNotification(
            String id,
            String displayName,
            String email,
            String faculty,
            String academicRank,
            String joinedAgo
    ) {}
}
