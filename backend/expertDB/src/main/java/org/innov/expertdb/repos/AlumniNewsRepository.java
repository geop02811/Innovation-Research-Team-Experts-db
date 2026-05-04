package org.innov.expertdb.repos;

import java.util.List;
import java.util.UUID;

import org.innov.expertdb.classes.AlumniNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlumniNewsRepository extends JpaRepository<AlumniNews, UUID> {
    List<AlumniNews> findAllByOrderByCreatedAtDesc();
}
