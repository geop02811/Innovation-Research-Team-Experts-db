package org.innov.expertdb.controllers;

import java.util.List;
import java.util.UUID;

import org.innov.expertdb.auth.dtos.competitions.CompetitionRequest;
import org.innov.expertdb.auth.dtos.competitions.CompetitionResponse;
import org.innov.expertdb.services.CompetitionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService competitionService;

    @GetMapping("/api/competitions")
    public ResponseEntity<List<CompetitionResponse>> getAll() {
        return ResponseEntity.ok(competitionService.getAll());
    }

    @PostMapping("/api/admin/competitions")
    public ResponseEntity<CompetitionResponse> create(@RequestBody CompetitionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(competitionService.create(req));
    }

    @PutMapping("/api/admin/competitions/{id}")
    public ResponseEntity<CompetitionResponse> update(@PathVariable UUID id, @RequestBody CompetitionRequest req) {
        return ResponseEntity.ok(competitionService.update(id, req));
    }

    @DeleteMapping("/api/admin/competitions/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        competitionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
