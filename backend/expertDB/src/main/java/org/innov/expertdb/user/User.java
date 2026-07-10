package org.innov.expertdb.user;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    // Expert profile fields
    @Column(name = "title_prefix")
    private String titlePrefix;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "contact_details", columnDefinition = "TEXT")
    private String contactDetails;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "research_interests", columnDefinition = "TEXT")
    private String researchInterests;

    @Column(name = "research_groups", columnDefinition = "TEXT")
    private String researchGroups;

    @Column(name = "academic_rank")
    private String academicRank;

    @Column(name = "university_email")
    private String universityEmail;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "highest_qualification")
    private String highestQualification;

    @Column(name = "professional_memberships", columnDefinition = "TEXT")
    private String professionalMemberships;

    @Column(name = "compliance_accreditation", columnDefinition = "TEXT")
    private String complianceAccreditation;

    @Column(name = "faculty")
    private String faculty;

    @Column(name = "department")
    private String department;

    @Column(name = "years_of_consultancy_experience")
    private String yearsOfConsultancyExperience;

    @Column(name = "consultancy_experience", columnDefinition = "TEXT")
    private String consultancyExperience;

    @Column(name = "professional_experiences", columnDefinition = "TEXT")
    private String professionalExperiences;

    @Column(name = "consultancy_availability")
    private String consultancyAvailability;

    /** Comma-separated list */
    @Column(name = "preferred_consultancy_types", columnDefinition = "TEXT")
    private String preferredConsultancyTypes;

    @Column(name = "geographic_scope")
    private String geographicScope;

    /** Comma-separated list */
    @Column(name = "skills_and_competences", columnDefinition = "TEXT")
    private String skillsAndCompetences;

    /** Comma-separated list */
    @Column(name = "languages_spoken", columnDefinition = "TEXT")
    private String languagesSpoken;

    @Column(name = "language_proficiencies", columnDefinition = "TEXT")
    private String languageProficiencies;

    /** Comma-separated list */
    @Column(name = "areas_of_expertise", columnDefinition = "TEXT")
    private String areasOfExpertise;

    /** Comma-separated list */
    @Column(name = "industrial_areas_of_expertise", columnDefinition = "TEXT")
    private String industrialAreasOfExpertise;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "profile_links", columnDefinition = "TEXT")
    private String profileLinks;

    @Column(name = "profile_photo_data_url", columnDefinition = "TEXT")
    private String profilePhotoDataUrl;

    @Column(name = "cv_data_url", columnDefinition = "TEXT")
    private String cvDataUrl;

    @Column(name = "university_id_data_url", columnDefinition = "TEXT")
    private String universityIdDataUrl;

    @Column(name = "token_version", columnDefinition = "INTEGER NOT NULL DEFAULT 0")
    private int tokenVersion = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public User() {}

    public User(UUID id, String name, String surname, String email, String passwordHash, Role role, AccountStatus status) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
    }
}
