package org.innov.expertdb.auth.dtos.viewer;

import java.util.List;

public record ViewerNotificationsResponse(
        int newExpertsCount,
        List<NewExpertNotification> newExperts
) {
    public record NewExpertNotification(
            String id,
            String displayName,
            String fullName,
            String faculty,
            String academicRank,
            String areasOfExpertise,
            String joinedAgo
    ) {}
}
