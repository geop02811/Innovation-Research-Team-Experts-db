package org.innov.expertdb.repos;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.innov.expertdb.user.AccountStatus;
import org.innov.expertdb.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    List<User> findByStatus(AccountStatus status);

    List<User> findByStatusAndCreatedAtAfter(AccountStatus status, Instant since);

    @Query("""
        SELECT u FROM User u
        WHERE u.status = :status
          AND u.fullName IS NOT NULL
          AND (
            LOWER(u.fullName)                    LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.name)                        LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.surname)                     LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.bio)                         LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.researchInterests)           LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.researchGroups)              LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.faculty)                     LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.department)                  LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.academicRank)                LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.highestQualification)        LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.areasOfExpertise)            LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.industrialAreasOfExpertise)  LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.skillsAndCompetences)        LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.languagesSpoken)             LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.consultancyExperience)       LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.consultancyAvailability)     LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.preferredConsultancyTypes)   LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.geographicScope)             LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.yearsOfConsultancyExperience) LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.titlePrefix)                 LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.complianceAccreditation)     LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.professionalMemberships)     LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.notes)                       LIKE LOWER(CONCAT('%', :q, '%'))
          )
        ORDER BY u.fullName ASC
        """)
    List<User> searchActive(@Param("status") AccountStatus status, @Param("q") String q);
}
