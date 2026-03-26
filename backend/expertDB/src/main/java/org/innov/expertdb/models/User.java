package org.innov.expertdb.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.time.LocalDateTime;

// 

@Entity
@Table(name = "users") // Maps to the Flyway 'users' table
@Data // Lombok: Generates Getters, Setters, toString, etc.
@NoArgsConstructor // Lombok: Generates a no-args constructor
@AllArgsConstructor // Lombok: Generates an all-args constructor
@Builder // Lombok: Enables the Builder pattern for this class
public class User {
    
    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "title_prefix")
    private TitlePrefix titlePrefix;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "academic_rank")
    private AcademicRank academicRank;

    @Column(name = "university_email", nullable = false, unique = true)
    private String universityEmail;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "highest_qualification")
    private String highestQualification;

    @Column(name = "professional_memberships")
    private String professionalMemberships;

    @Column(name = "compliance_accreditation")
    private String complianceAccreditation;

    private String faculty;

    @Enumerated(EnumType.STRING)
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(name = "years_consultancy_experience")
    private YearsOfExperience yearsConsultancyExperience;

    @Column(name = "consultancy_experience")
    private String consultancyExperience;

    @Enumerated(EnumType.STRING)
    @Column(name = "consultancy_availability")
    private Availability consultancyAvailability;

    @Column(name = "preferred_consultancy_types")
    private String preferredConsultancyTypes;

    @Enumerated(EnumType.STRING)
    @Column(name = "geographic_scope")
    private GeographicScope geographicScope;

    @Column(name = "skills_competences")
    private String skillsCompetences;

    @Column(name = "languages_spoken")
    private String languagesSpoken;

    @Column(name = "areas_of_expertise")
    private String areasOfExpertise;

    @Column(name = "industrial_areas_expertise")
    private String industrialAreasExpertise;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}