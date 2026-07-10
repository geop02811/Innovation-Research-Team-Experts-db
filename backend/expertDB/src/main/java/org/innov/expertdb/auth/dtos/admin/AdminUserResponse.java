package org.innov.expertdb.auth.dtos.admin;

import java.util.UUID;

import org.innov.expertdb.user.AccountStatus;
import org.innov.expertdb.user.Role;

public record AdminUserResponse(
        UUID id,
        String name,
        String surname,
        String email,
        Role role,
        AccountStatus status,

        // Expert profile fields
        String titlePrefix,
        String fullName,
        String contactDetails,
        String bio,
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
        String preferredConsultancyTypes,
        String geographicScope,
        String skillsAndCompetences,
        String languagesSpoken,
        String languageProficiencies,
        String areasOfExpertise,
        String industrialAreasOfExpertise,
        String notes,
        String profilePhotoDataUrl,
        String cvDataUrl,
        String universityIdDataUrl,
        String professionalExperiences,
        String profileLinks
) {}
