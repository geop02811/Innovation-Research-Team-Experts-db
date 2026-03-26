package org.innov.expertdb.repositories;

import org.innov.expertdb.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // Spring automatically knows how to query by email just by reading this method name!
    Optional<User> findByEmail(String email);
}
