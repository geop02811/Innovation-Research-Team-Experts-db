package org.innov.expertdb.auth.dtos.events;

import java.time.LocalDate;

public record EventRequest(
        String title,
        String description,
        LocalDate eventDate,
        String category,
        String location) {
}
