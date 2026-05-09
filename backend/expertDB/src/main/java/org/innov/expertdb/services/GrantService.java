package org.innov.expertdb.services;

import java.util.List;
import java.util.UUID;

import org.innov.expertdb.auth.dtos.grants.GrantRequest;
import org.innov.expertdb.auth.dtos.grants.GrantResponse;
import org.innov.expertdb.classes.Grant;
import org.innov.expertdb.repos.GrantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GrantService {

    private final GrantRepository grantRepository;

    public List<GrantResponse> getAll() {
        return grantRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(GrantResponse::from)
                .toList();
    }

    public List<GrantResponse> search(String q, String status) {
        List<Grant> results = (q != null && !q.isBlank())
                ? grantRepository.searchAll(q.trim())
                : grantRepository.findAllByOrderByCreatedAtDesc();
        if (status != null && !status.isBlank()) {
            // status may be comma-separated (e.g. "OPEN,INTERNAL"); match if grant has ANY of them
            java.util.Set<String> requested = java.util.Arrays.stream(status.trim().toUpperCase().split(","))
                    .map(String::trim)
                    .collect(java.util.stream.Collectors.toSet());
            results = results.stream().filter(g -> {
                java.util.Set<String> grantStatuses = java.util.Arrays.stream(g.getStatus().split(","))
                        .map(String::trim)
                        .collect(java.util.stream.Collectors.toSet());
                return requested.stream().allMatch(grantStatuses::contains);
            }).toList();
        }
        return results.stream().map(GrantResponse::from).toList();
    }

    @Transactional
    public GrantResponse create(GrantRequest req) {
        Grant g = new Grant();
        apply(g, req);
        return GrantResponse.from(grantRepository.save(g));
    }

    @Transactional
    public GrantResponse update(UUID id, GrantRequest req) {
        Grant g = grantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grant not found"));
        apply(g, req);
        return GrantResponse.from(grantRepository.save(g));
    }

    @Transactional
    public void delete(UUID id) {
        grantRepository.deleteById(id);
    }

    private void apply(Grant g, GrantRequest req) {
        g.setFunder(req.funder());
        g.setTitle(req.title());
        g.setDescription(req.description());
        g.setAmount(req.amount());
        g.setClosingDate(req.closingDate());
        g.setStatus(req.status() != null ? req.status() : "OPEN");
        g.setCategory(req.category());
        g.setApplyUrl(req.applyUrl());
        g.setFeatured(req.featured());
    }
}
