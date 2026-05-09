package org.innov.expertdb.auth.dtos.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

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
        /** Comma-separated */
        String areasOfExpertise,
        /** Comma-separated */
        String industrialAreasOfExpertise,
        String notes,
        String profilePhotoDataUrl,
        String cvDataUrl,
        String universityIdDataUrl) { }


