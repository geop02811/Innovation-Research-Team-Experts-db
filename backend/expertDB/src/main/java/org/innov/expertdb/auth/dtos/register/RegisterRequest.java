package org.innov.expertdb.auth.dtos.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Surname is required")
        String surname,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        String password,

        // Expert profile fields
        String titlePrefix,
        String fullName,
        String contactDetails,
        String academicRank,
        @NotBlank(message = "University email is required")
        @Pattern(
                regexp = "(?i)^(?:[a-z]+(?:-[a-z]+)*\\.[a-z]+(?:-[a-z]+)*@students\\.uz\\.ac\\.zw|[a-z][a-z]+@admin\\.uz\\.ac\\.zw|[a-z][a-z0-9._-]*@(?!admin\\.|students\\.)[a-z][a-z0-9-]*\\.uz\\.ac\\.zw)$",
                message = "University email must be a valid UZ student, admin, or academic staff address"
        )
        String universityEmail,
        String phoneNumber,
        String highestQualification,
        String professionalMemberships,
        String complianceAccreditation,
        String faculty,
        String department,
        String yearsOfConsultancyExperience,
        String consultancyExperience,
        String consultancyAvailability,
        /** Comma-separated */
        String preferredConsultancyTypes,
        String geographicScope,
        /** Comma-separated */
        String skillsAndCompetences,
        /** Comma-separated */
        String languagesSpoken,
        String languageProficiencies,
        /** Comma-separated */
        String areasOfExpertise,
        /** Comma-separated */
        String industrialAreasOfExpertise,
        String notes,
        String profilePhotoDataUrl,
        String cvDataUrl,
        String universityIdDataUrl,
        String professionalExperiences,
        String profileLinks) { }


