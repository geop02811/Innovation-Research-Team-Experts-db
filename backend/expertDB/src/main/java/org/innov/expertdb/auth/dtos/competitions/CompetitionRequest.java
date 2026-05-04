package org.innov.expertdb.auth.dtos.competitions;

import java.time.LocalDate;

public record CompetitionRequest(
        String title,
        String description,
        LocalDate deadline,
        String status,
        String prize,
        String ctaLabel,
        String ctaUrl) {
}
