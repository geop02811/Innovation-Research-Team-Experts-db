package org.innov.expertdb.repos;

import java.util.List;
import java.util.UUID;

import org.innov.expertdb.classes.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findAllByOrderByEventDateAsc();
}
