package org.innov.expertdb.repos;

import java.util.List;
import java.util.UUID;

import org.innov.expertdb.classes.Grant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GrantRepository extends JpaRepository<Grant, UUID> {
    List<Grant> findAllByOrderByCreatedAtDesc();

    @Query("""
        SELECT g FROM Grant g
        WHERE (
            LOWER(g.title)       LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(g.funder)      LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(g.description) LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(g.category)    LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(g.status)      LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(g.amount)      LIKE LOWER(CONCAT('%', :q, '%'))
        )
        ORDER BY g.createdAt DESC
        """)
    List<Grant> searchAll(@Param("q") String q);
}
