package org.innov.expertdb.controllers;

import java.util.List;
import java.util.UUID;

import org.innov.expertdb.auth.dtos.grants.GrantRequest;
import org.innov.expertdb.auth.dtos.grants.GrantResponse;
import org.innov.expertdb.services.GrantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GrantController {

    private final GrantService grantService;

    /** Public — anyone can browse grants. Supports ?q= full-text search and ?status= filter. */
    @GetMapping("/api/grants")
    public ResponseEntity<List<GrantResponse>> getAll(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status) {
        if ((q != null && !q.isBlank()) || (status != null && !status.isBlank())) {
            return ResponseEntity.ok(grantService.search(q, status));
        }
        return ResponseEntity.ok(grantService.getAll());
    }

    /** Admin only — create */
    @PostMapping("/api/admin/grants")
    public ResponseEntity<GrantResponse> create(@RequestBody GrantRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(grantService.create(req));
    }

    /** Admin only — update */
    @PutMapping("/api/admin/grants/{id}")
    public ResponseEntity<GrantResponse> update(@PathVariable UUID id, @RequestBody GrantRequest req) {
        return ResponseEntity.ok(grantService.update(id, req));
    }

    /** Admin only — delete */
    @DeleteMapping("/api/admin/grants/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        grantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
