package org.innov.expertdb.auth.dtos.grants;

import java.time.LocalDate;
import java.util.UUID;

import org.innov.expertdb.classes.Grant;

public record GrantResponse(
        UUID id,
        String funder,
        String title,
        String description,
        String amount,
        LocalDate closingDate,
        String status,
        String category,
        String applyUrl,
        boolean featured) {

    public static GrantResponse from(Grant g) {
        return new GrantResponse(
                g.getId(),
                g.getFunder(),
                g.getTitle(),
                g.getDescription(),
                g.getAmount(),
                g.getClosingDate(),
                g.getStatus(),
                g.getCategory(),
                g.getApplyUrl(),
                g.isFeatured());
    }
}
