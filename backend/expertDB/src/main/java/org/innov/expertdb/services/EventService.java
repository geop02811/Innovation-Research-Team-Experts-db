package org.innov.expertdb.services;

import java.util.List;
import java.util.UUID;

import org.innov.expertdb.auth.dtos.events.EventRequest;
import org.innov.expertdb.auth.dtos.events.EventResponse;
import org.innov.expertdb.classes.Event;
import org.innov.expertdb.repos.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<EventResponse> getAll() {
        return eventRepository.findAllByOrderByEventDateAsc()
                .stream().map(EventResponse::from).toList();
    }

    @Transactional
    public EventResponse create(EventRequest req) {
        Event e = new Event();
        apply(e, req);
        return EventResponse.from(eventRepository.save(e));
    }

    @Transactional
    public EventResponse update(UUID id, EventRequest req) {
        Event e = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        apply(e, req);
        return EventResponse.from(eventRepository.save(e));
    }

    @Transactional
    public void delete(UUID id) {
        eventRepository.deleteById(id);
    }

    private void apply(Event e, EventRequest req) {
        e.setTitle(req.title());
        e.setDescription(req.description());
        e.setEventDate(req.eventDate());
        e.setCategory(req.category());
        e.setLocation(req.location());
    }
}
