package org.innov.expertdb.repos;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.innov.expertdb.user.AccountStatus;
import org.innov.expertdb.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    List<User> findByStatus(AccountStatus status);

    List<User> findByStatusAndCreatedAtAfter(AccountStatus status, Instant since);
}
