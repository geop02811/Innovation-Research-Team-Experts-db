package org.innov.expertdb.auth.dtos.events;

import java.time.LocalDate;
import java.util.UUID;

import org.innov.expertdb.classes.Event;

public record EventResponse(
        UUID id,
        String title,
        String description,
        LocalDate eventDate,
        String category,
        String location) {

    public static EventResponse from(Event e) {
        return new EventResponse(e.getId(), e.getTitle(), e.getDescription(),
                e.getEventDate(), e.getCategory(), e.getLocation());
    }
}
