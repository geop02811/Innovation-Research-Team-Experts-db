package org.innov.expertdb.controllers;

import java.util.List;
import java.util.UUID;

import org.innov.expertdb.auth.dtos.alumni.AlumniNewsRequest;
import org.innov.expertdb.auth.dtos.alumni.AlumniNewsResponse;
import org.innov.expertdb.services.AlumniNewsService;
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
public class AlumniNewsController {

    private final AlumniNewsService alumniNewsService;

    @GetMapping("/api/alumni-news")
    public ResponseEntity<List<AlumniNewsResponse>> getAll() {
        return ResponseEntity.ok(alumniNewsService.getAll());
    }

    @PostMapping("/api/admin/alumni-news")
    public ResponseEntity<AlumniNewsResponse> create(@RequestBody AlumniNewsRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alumniNewsService.create(req));
    }

    @PutMapping("/api/admin/alumni-news/{id}")
    public ResponseEntity<AlumniNewsResponse> update(@PathVariable UUID id, @RequestBody AlumniNewsRequest req) {
        return ResponseEntity.ok(alumniNewsService.update(id, req));
    }

    @DeleteMapping("/api/admin/alumni-news/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        alumniNewsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
