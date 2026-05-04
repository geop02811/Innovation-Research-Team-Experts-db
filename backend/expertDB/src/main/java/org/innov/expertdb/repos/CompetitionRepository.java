package org.innov.expertdb.repos;

import java.util.List;
import java.util.UUID;

import org.innov.expertdb.classes.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, UUID> {
    List<Competition> findAllByOrderByCreatedAtDesc();
}
