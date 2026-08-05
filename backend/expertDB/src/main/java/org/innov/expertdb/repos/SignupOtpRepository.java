package org.innov.expertdb.repos;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.innov.expertdb.auth.signup.SignupOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignupOtpRepository extends JpaRepository<SignupOtp, UUID> {
    Optional<SignupOtp> findTopByEmailAndUsedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(
            String email,
            Instant now
    );
}
