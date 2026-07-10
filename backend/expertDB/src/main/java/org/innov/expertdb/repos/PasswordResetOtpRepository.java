package org.innov.expertdb.repos;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.innov.expertdb.auth.password.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, UUID> {
    Optional<PasswordResetOtp> findTopByEmailAndUsedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(
            String email,
            Instant now
    );
}