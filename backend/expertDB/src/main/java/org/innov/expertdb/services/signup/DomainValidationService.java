package org.innov.expertdb.services.signup;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Restricts signup to a configurable set of email domains. Matching is suffix-based, so a single
 * entry like "uz.ac.zw" already covers every subdomain (e.g. students.uz.ac.zw, ceic.uz.ac.zw).
 * Add more entries to app.signup.allowed-email-domains for domains outside that hierarchy.
 */
@Service
public class DomainValidationService {

    private final List<String> allowedDomains;

    public DomainValidationService(
            @Value("${app.signup.allowed-email-domains:uz.ac.zw}") String allowedDomainsConfig
    ) {
        this.allowedDomains = Arrays.stream(allowedDomainsConfig.split(","))
                .map(String::trim)
                .filter(domain -> !domain.isEmpty())
                .map(String::toLowerCase)
                .toList();
    }

    public void validate(String email) {
        String domain = extractDomain(email);
        boolean allowed = allowedDomains.stream()
                .anyMatch(allowedDomain -> domain.equals(allowedDomain) || domain.endsWith("." + allowedDomain));

        if (!allowed) {
            throw new RuntimeException(
                    "Registration is restricted to University of Zimbabwe email addresses (e.g. @uz.ac.zw)."
            );
        }
    }

    private String extractDomain(String email) {
        String normalized = email.trim().toLowerCase();
        int atIndex = normalized.lastIndexOf('@');
        if (atIndex < 0 || atIndex == normalized.length() - 1) {
            throw new RuntimeException("Email address is invalid.");
        }
        return normalized.substring(atIndex + 1);
    }
}
