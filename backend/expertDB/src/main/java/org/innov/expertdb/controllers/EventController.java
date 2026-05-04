package org.innov.expertdb.controllers;

import java.util.List;
import java.util.UUID;

import org.innov.expertdb.auth.dtos.events.EventRequest;
import org.innov.expertdb.auth.dtos.events.EventResponse;
import org.innov.expertdb.services.EventService;
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
public class EventController {

    private final EventService eventService;

    @GetMapping("/api/events")
    public ResponseEntity<List<EventResponse>> getAll() {
        return ResponseEntity.ok(eventService.getAll());
    }

    @PostMapping("/api/admin/events")
    public ResponseEntity<EventResponse> create(@RequestBody EventRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(req));
    }

    @PutMapping("/api/admin/events/{id}")
    public ResponseEntity<EventResponse> update(@PathVariable UUID id, @RequestBody EventRequest req) {
        return ResponseEntity.ok(eventService.update(id, req));
    }

    @DeleteMapping("/api/admin/events/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
