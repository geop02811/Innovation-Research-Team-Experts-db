package org.innov.expertdb.auth.dtos.grants;

import java.time.LocalDate;

public record GrantRequest(
        String funder,
        String title,
        String description,
        String amount,
        LocalDate closingDate,
        String status,
        String category,
        String applyUrl,
        boolean featured) {
}
