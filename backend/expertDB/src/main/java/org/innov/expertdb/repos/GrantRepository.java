package org.innov.expertdb.repos;

import java.util.List;
import java.util.UUID;

import org.innov.expertdb.classes.Grant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrantRepository extends JpaRepository<Grant, UUID> {
    List<Grant> findAllByOrderByCreatedAtDesc();
}
