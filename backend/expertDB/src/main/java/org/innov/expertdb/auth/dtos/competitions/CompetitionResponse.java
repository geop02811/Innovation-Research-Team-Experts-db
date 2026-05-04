package org.innov.expertdb.auth.dtos.competitions;

import java.time.LocalDate;
import java.util.UUID;

import org.innov.expertdb.classes.Competition;

public record CompetitionResponse(
        UUID id,
        String title,
        String description,
        LocalDate deadline,
        String status,
        String prize,
        String ctaLabel,
        String ctaUrl) {

    public static CompetitionResponse from(Competition c) {
        return new CompetitionResponse(c.getId(), c.getTitle(), c.getDescription(),
                c.getDeadline(), c.getStatus(), c.getPrize(), c.getCtaLabel(), c.getCtaUrl());
    }
}
